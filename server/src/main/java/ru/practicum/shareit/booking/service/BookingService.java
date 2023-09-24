package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookState;

import java.util.List;

public interface BookingService {
    ResponseBookingDto add(RequestBookingDto requestBookingDto, long userId);

    ResponseBookingDto accept(long bookingId, long userId, boolean approved);

    ResponseBookingDto get(long userId, long bookingId);

    List<ResponseBookingDto> getAll(long userId, BookState state, int from, int size);

    List<ResponseBookingDto> getAllByUser(long userId, BookState state, int from, int size);
}