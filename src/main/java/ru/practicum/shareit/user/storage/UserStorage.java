package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User getById(long id);

    User add(User user);

    User update(User user, long userId);

    void delete(long id);
}
