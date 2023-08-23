package ru.practicum.shareit.exceptions.booking;

public class BookingStatusException extends RuntimeException {
    public BookingStatusException(String message) {
        super(message);
    }
}
