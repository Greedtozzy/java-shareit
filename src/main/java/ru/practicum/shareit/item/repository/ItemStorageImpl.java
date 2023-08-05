package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemsOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final UserStorage userStorage;
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    @Override
    public List<Item> getAll(long userId) {
        userStorage.getById(userId);
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item get(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item add(long userId, Item item) {
        userStorage.getById(userId);
        item.setId(id);
        item.setOwnerId(userId);
        items.put(id++, item);
        return item;
    }

    @Override
    public Item update(long userId, Item item, long itemId) {
        userStorage.getById(userId);
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(String.format("Item by id %d not found", itemId));
        }
        if (items.get(itemId).getOwnerId() != userId) {
            throw new ItemsOwnerException(String.format("User by id %d isn't item owner", userId));
        }
        if (item.getName() != null) items.get(itemId).setName(item.getName());
        if (item.getDescription() != null) items.get(itemId).setDescription(item.getDescription());
        if (item.getAvailable() != null) items.get(itemId).setAvailable(item.getAvailable());
        return get(itemId);
    }

    @Override
    public void delete(long userId, long itemId) {
        userStorage.getById(userId);
        if (items.containsKey(itemId)) {
            if (items.get(itemId).getOwnerId() == userId) {
                items.remove(itemId);
            } else {
                throw new ItemsOwnerException(String.format("User by id %d isn't item owner", userId));
            }
        }
    }
}
