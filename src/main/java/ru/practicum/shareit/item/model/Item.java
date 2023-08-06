package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    long id;
    @NotBlank
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
}
