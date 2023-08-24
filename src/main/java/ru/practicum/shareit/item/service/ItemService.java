package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll(long userId);

    ItemDto get(long itemId, long userId);

    List<ItemDto> search(String text);

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto, long itemId);

    void delete(long userId, long itemId);

    CommentDto addComment(CommentDto dto, long userId, long itemId);
}
