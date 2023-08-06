package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    BookStatus status;
}