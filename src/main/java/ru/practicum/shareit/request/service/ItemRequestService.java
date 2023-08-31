package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> get(long userId, int from, int size);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto getById(long userId, long requestId);

    void delete(long userId, long requestId);
}
