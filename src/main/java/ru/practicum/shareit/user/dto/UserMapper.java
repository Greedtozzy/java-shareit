package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto userDto) {
         return new User(userDto.getId(),
                 userDto.getName() != null ? userDto.getName() : null,
                 userDto.getEmail() != null ? userDto.getEmail() : null);
    }
}
