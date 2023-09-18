package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.exceptions.request.ItemRequestNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto add(long userId, ItemRequestDto requestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto);
        itemRequest.setRequestor(UserMapper.toUser(userService.getById(userId)));
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(repository.save(itemRequest));
        return addItems(dto);
    }

    @Override
    public List<ItemRequestDto> get(long userId, int from, int size) {
        if (from < 0 || size < 1) {
            throw new PaginationException("From must be positive or zero, size must be positive.");
        }
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return repository.findAllByRequestorId(userId, pageable)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .map(this::addItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        if (from < 0 || size < 1) {
            throw new PaginationException("From must be positive or zero, size must be positive.");
        }
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return repository.findAllByRequestorIdIsNot(userId, pageable)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .map(this::addItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        userService.getById(userId);
        return addItems(ItemRequestMapper.toItemRequestDto(repository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(String.format("Request by id %d not found", requestId)))));
    }

    @Override
    public void delete(long userId, long requestId) {
        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(String.format("Request by id %d not found", requestId)));
        if (request.getRequestor().getId() != userId) {
            throw new RuntimeException();
        }
        repository.deleteById(requestId);
    }

    private ItemRequestDto addItems(ItemRequestDto dto) {
        dto.setItems(itemRepository.findAllByRequestId(dto.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
