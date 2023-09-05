package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.comment.CommentException;
import ru.practicum.shareit.exceptions.item.ItemNotFoundException;
import ru.practicum.shareit.exceptions.item.ItemsOwnerException;
import ru.practicum.shareit.exceptions.request.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    private final User user = new User(1, "name", "email@email.com");
    private final User user1 = new User(2, "name1", "email1@email.com");
    private final UserDto userDto = new UserDto(1, "name", "email@email.com");
    private final UserDto user1Dto = new UserDto(2, "name1", "email1@email.com");
    private final ItemRequest itemRequest = new ItemRequest(1, "request", user1, LocalDateTime.now());
    private final Item item = new Item(1, "item", "description",
            true, user, itemRequest,
            null, null, new ArrayList<>());
    private final Booking lastBooking = new Booking(1,
            LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0),
            item, user1, BookStatus.APPROVED);
    private final Booking nextBooking = new Booking(2,
            LocalDateTime.of(2023, 10, 10, 0, 0),
            LocalDateTime.of(2023, 10, 20, 0, 0),
            item, user1, BookStatus.APPROVED);
    private final BookingDto lastBookingDto = new BookingDto(1,
            LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0),
            2);
    private final BookingDto nextBookingDto = new BookingDto(2,
            LocalDateTime.of(2023, 10, 10, 0, 0),
            LocalDateTime.of(2023, 10, 20, 0, 0),
            2);
    private final ItemDto itemDto = new ItemDto(1, "item", "description",
            true, lastBookingDto,
            nextBookingDto, new ArrayList<>(), 1);
    private final ItemDto itemDtoNoBookings = new ItemDto(1, "item", "description",
            true, null,
            null, new ArrayList<>(), 1);
    private final Comment comment = new Comment(1, "text", item, user1, LocalDateTime.of(2023, 8, 31, 0, 0));
    private final CommentDto commentDto = new CommentDto(1, "text", "name1", LocalDateTime.of(2023, 8, 31, 0, 0));

    @Test
    void addTest() {
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        assertEquals(itemService.add(1, itemDto), itemDtoNoBookings);
    }

    @Test
    void addItemFromUserNotExistTest() {
        when(userService.getById(anyLong()))
                .thenThrow(new UserNotFoundException(String.format("User by id %d not found", 1)));
        Exception e = assertThrows(UserNotFoundException.class, () -> itemService.add(1, itemDto));
        assertEquals(e.getMessage(), "User by id 1 not found");
    }

    @Test
    void addItemWithWrongRequestIdTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ItemRequestNotFoundException.class, () -> itemService.add(1, itemDto));
        assertEquals(e.getMessage(), "Request by id 1 not found");
    }

    @Test
    void getByIdIfUserIsNotOwnerTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong()))
                        .thenReturn(new ArrayList<>());
        assertEquals(itemService.get(1,2), itemDtoNoBookings);
    }

    @Test
    void getByIdIfUserIsOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemIdAndStatusNot(anyLong(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        assertEquals(itemService.get(1,1), itemDto);
    }

    @Test
    void getByIdIfNotExistTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ItemNotFoundException.class, () -> itemService.get(1, 1));
        assertEquals(e.getMessage(), "Item by id 1 not found");
    }

    @Test
    void getAllTest() {
        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdAndStatusNot(anyLong(), any()))
                        .thenReturn(List.of(lastBooking, nextBooking));
        assertEquals(itemService.getAll(1, 0, 10), List.of(itemDto));
    }

    @Test
    void searchTest() {
        when(itemRepository.search(anyString(), any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdAndStatusNot(anyLong(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        assertEquals(itemService.search("name", 0, 10), List.of(itemDto));
    }

    @Test
    void searchWithBlankTextTest() {
        assertEquals(itemService.search("", 0, 10), new ArrayList<>());
    }

    @Test
    void updateTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        ItemDto updateItem = new ItemDto(1, "updatedName",
                "updatedDescription",
                true,
                null,
                null,
                new ArrayList<>(),
                0);
        when(itemRepository.save(any()))
                        .thenReturn(ItemMapper.toItem(updateItem));
        assertEquals(itemService.update(1, updateItem, 1), updateItem);
    }

    @Test
    void updateByNotOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Exception e = assertThrows(ItemsOwnerException.class, () -> itemService.update(2, itemDto, 1));
        assertEquals(e.getMessage(), "User by id 2 is not an owner");
    }

    @Test
    void updateItemIsNotExist() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ItemNotFoundException.class, () -> itemService.update(1, itemDto, 1));
        assertEquals(e.getMessage(), "Item by id 1 not found");
    }

    @Test
    void deleteTest() {
        itemService.delete(1, 1);
        Mockito.verify(itemRepository).deleteById(1L);
    }

    @Test
    void addComment() {
        when(bookingRepository
                        .findAllByBookerIdAndItemIdAndStatusIsAndStartIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(lastBooking));
        when(itemRepository.findById(anyLong()))
                        .thenReturn(Optional.of(item));
        when(userService.getById(anyLong()))
                        .thenReturn(user1Dto);
        when(commentRepository.save(any()))
                .thenReturn(comment);
        assertEquals(itemService.addComment(commentDto, 2, 1), commentDto);
    }

    @Test
    void addCommentFromWrongUser() {
        when(bookingRepository
                        .findAllByBookerIdAndItemIdAndStatusIsAndStartIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());
        Exception e = assertThrows(CommentException.class, () -> itemService.addComment(commentDto, 2, 1));
        assertEquals(e.getMessage(), "User by id 2 do not use item by id 1");
    }
}
