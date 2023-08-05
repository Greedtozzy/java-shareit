package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getAll(long userId);

    Item get(long itemId);

    List<Item> search(String text);

    Item add(long userId, Item item);

    Item update(long userId, Item item, long itemId);

    void delete(long userId, long itemId);
}
