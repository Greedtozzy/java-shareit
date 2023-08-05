package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage repository;
    private final ItemMapper mapper = new ItemMapper();

    @Override
    public List<ItemDto> getAll(long userId) {
        return repository.getAll(userId).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto get(long itemId) {
        return mapper.toItemDto(repository.get(itemId));
    }

    @Override
    public List<ItemDto> search(String text) {
        return repository.search(text).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        return mapper.toItemDto(repository.add(userId, mapper.toItem(itemDto, userId)));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto, long itemId) {
        return mapper.toItemDto(repository.update(userId, mapper.toItem(itemDto, userId), itemId));
    }

    @Override
    public void delete(long userId, long itemId) {
        repository.delete(userId, itemId);
    }
}
