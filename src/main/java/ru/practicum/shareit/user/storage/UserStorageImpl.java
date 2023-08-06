package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
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
        emailUniqSet.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user, long userId) {
        User updatedUser = getById(userId);
        if (user.getName() != null) updatedUser.setName(user.getName());
        if (user.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (isExistEmail(user)) {
                throw new EmailAlreadyExistException(String.format("User with email %s already exist", user.getEmail()));
            }
            emailUniqSet.remove(updatedUser.getEmail());
            emailUniqSet.add(user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }
        return updatedUser;
    }

    @Override
    public void delete(long id) {
        emailUniqSet.remove(users.get(id).getEmail());
        users.remove(id);
    }

    private boolean isExistEmail(User user) {
        return emailUniqSet.contains(user.getEmail());
    }
}
