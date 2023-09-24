package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto getById(long id) {
        return UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by id %d not found", id))));
    }

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
            return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, long id) {
        UserDto updatedUser = getById(id);
        if (userDto.getName() != null) updatedUser.setName(userDto.getName());
        if (userDto.getEmail() != null) updatedUser.setEmail(userDto.getEmail());
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(updatedUser)));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        repository.deleteById(id);
    }
}
