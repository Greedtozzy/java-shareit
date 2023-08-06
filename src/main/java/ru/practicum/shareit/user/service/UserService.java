package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(long id);

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto, long id);

    void deleteById(long id);
}
