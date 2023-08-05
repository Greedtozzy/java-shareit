package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long userId = 1;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long id) {
        if (!users.containsKey(id)) throw new UserNotFoundException(String.format("User by id %d not found", id));
        return users.get(id);
    }

    @Override
    public User add(User user) {
        if (isExistEmail(user)) {
            throw new UserAlreadyExistException(String.format("User with email %s already exist", user.getEmail()));
        }
        user.setId(userId);
        users.put(userId++, user);
        return user;
    }

    @Override
    public User update(User user, long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(String.format("User by id %d not found", userId));
        }
        if (user.getName() != null) users.get(userId).setName(user.getName());
        if (user.getEmail() != null && !users.get(userId).getEmail().equals(user.getEmail())) {
            if (isExistEmail(user)) {
                throw new EmailAlreadyExistException(String.format("User with email %s already exist", user.getEmail()));
            }
            users.get(userId).setEmail(user.getEmail());
        }
        return users.get(userId);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    private boolean isExistEmail(User user) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));
    }
}
