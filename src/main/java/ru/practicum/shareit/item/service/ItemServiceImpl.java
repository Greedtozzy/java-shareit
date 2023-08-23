package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.comment.CommentException;
import ru.practicum.shareit.exceptions.item.ItemNotFoundException;
import ru.practicum.shareit.exceptions.item.ItemsOwnerException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional
    public List<ItemDto> getAll(long userId) {
        return repository.findAllByOwnerId(userId).stream()
                .map(this::addComments)
                .map(this::addLastAndNextBooking)
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto get(long itemId, long userId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item by id %d not found", itemId)));
        addComments(item);
        if (userId == item.getOwner().getId()) addLastAndNextBooking(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return repository.search(text).stream()
                .map(this::addComments)
                .map(this::addLastAndNextBooking)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto add(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.getById(userId)));
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long userId, ItemDto itemDto, long itemId) {
        userService.getById(userId);
        Item updatedItem = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item by id %d not found", itemId)));
        if (updatedItem.getOwner().getId() != userId) {
            throw new ItemsOwnerException(String.format("User by id %d i not an owner", userId));
        }
        if (itemDto.getName() != null) updatedItem.setName(itemDto.getName());
        if (itemDto.getAvailable() != null) updatedItem.setAvailable(itemDto.getAvailable());
        if (itemDto.getDescription() != null) updatedItem.setDescription(itemDto.getDescription());
        return ItemMapper.toItemDto(repository.save(updatedItem));
    }

    @Override
    @Transactional
    public void delete(long userId, long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    @Transactional
    public ResponseCommentDto addComment(RequestCommentDto dto, long userId, long itemId) {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndItemIdAndStatusIsAndStartIsBefore(userId, itemId, BookStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new CommentException(String.format("User by id %d do not use item by id %d", userId, itemId));
        }
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item by id %d not found", itemId))));
        comment.setAuthor(UserMapper.toUser(userService.getById(userId)));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toResponseCommentDto(commentRepository.save(comment));
    }

    private Item addLastAndNextBooking(Item item) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusNot(item.getId(), BookStatus.REJECTED);
        Booking lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        if (lastBooking != null) item.setLastBooking(lastBooking);
        if (nextBooking != null) item.setNextBooking(nextBooking);
        return item;
    }

    private Item addComments(Item item) {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        item.setComments(Objects.requireNonNullElseGet(comments, ArrayList::new));
        return item;
    }
}
