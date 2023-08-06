package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemsOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final UserStorage userStorage;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> userItemIndex = new LinkedHashMap<>();
    private long id = 1;

    @Override
    public List<Item> getAll(long userId) {
        userStorage.getById(userId);
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item get(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(String.format("Item by id %d not found", itemId));
        }
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
        item.setOwner(userStorage.getById(userId));
        items.put(id++, item);
        if (!userItemIndex.containsKey(userId)) userItemIndex.put(userId, new ArrayList<>());
        userItemIndex.get(userId).add(item.getId());
        return item;
    }

    @Override
    public Item update(long userId, Item item, long itemId) {
        userStorage.getById(userId);
        Item updatedItem = items.get(itemId);
        if (updatedItem.getOwner().getId() != userId) {
            throw new ItemsOwnerException(String.format("User by id %d isn't item owner", userId));
        }
        if (item.getName() != null) updatedItem.setName(item.getName());
        if (item.getDescription() != null) updatedItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) updatedItem.setAvailable(item.getAvailable());
        return updatedItem;
    }

    @Override
    public void delete(long userId, long itemId) {
        userStorage.getById(userId);
        if (items.containsKey(itemId)) {
            if (items.get(itemId).getOwner().getId() == userId) {
                items.remove(itemId);
            } else {
                throw new ItemsOwnerException(String.format("User by id %d isn't item owner", userId));
            }
        }
    }
}
