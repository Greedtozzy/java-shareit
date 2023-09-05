package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;
    private final User user = new User(1, "name", "email@email.com");
    private final UserDto userDto = new UserDto(1, "name", "email@email.com");

    @Test
    void addTest() {
        when(repository.save(any()))
                .thenReturn(user);
        assertEquals(service.add(userDto), userDto);
    }

    @Test
    void getAllTest() {
        when(repository.findAll())
                .thenReturn(List.of(user));
        assertEquals(service.getAll(), List.of(userDto));
    }

    @Test
    void getAllWhenNoUsersTest() {
        when(repository.findAll())
                .thenReturn(new ArrayList<>());
        assertEquals(service.getAll(), new ArrayList<>());
    }

    @Test
    void getByIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertEquals(service.getById(1), userDto);
    }

    @Test
    void getByIdIfNotExistTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(UserNotFoundException.class, () -> service.getById(1));
        assertEquals(e.getMessage(), "User by id 1 not found");
    }

    @Test
    void updateTest() {
        User updatedUser = new User(1, "upName", "upEmail@email.com");
        UserDto updatedDto = new UserDto(1, "upName", "upEmail@email.com");
        when(repository.findById(anyLong()))
                        .thenReturn(Optional.of(user));
        when(repository.save(any()))
                .thenReturn(updatedUser);
        assertEquals(service.update(updatedDto, 1), updatedDto);
    }

    @Test
    void updateWhenUserIsNotExist() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(UserNotFoundException.class, () -> service.update(userDto, 1));
        assertEquals(e.getMessage(), "User by id 1 not found");
    }

    @Test
    void deleteTest() {
        repository.deleteById(1L);
        Mockito.verify(repository).deleteById(1L);
    }
}
