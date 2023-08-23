package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.user.dto.ShortUserDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseBookingDto {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    ShortItemDto item;
    ShortUserDto booker;
    BookStatus status;
}
