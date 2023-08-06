package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getAll() {
        return storage.getAll().stream().map(mapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        return mapper.toUserDto(storage.getById(id));
    }

    @Override
    public UserDto add(UserDto userDto) {
        return mapper.toUserDto(storage.add(mapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        return mapper.toUserDto(storage.update(mapper.toUser(userDto), id));
    }

    @Override
    public void deleteById(long id) {
        storage.delete(id);
    }
}
