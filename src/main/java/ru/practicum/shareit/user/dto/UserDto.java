package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDto {
    long id;
    @NotBlank(groups = {NewUser.class})
    String name;
    @NotNull(groups = {NewUser.class})
    @Email
    String email;

    public interface NewUser {
    }
}
