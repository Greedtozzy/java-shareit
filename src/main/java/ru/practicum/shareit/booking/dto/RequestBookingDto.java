package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestBookingDto {
    long itemId;
    @Future(groups = {RequestBookingDto.NewBooking.class})
    @NotNull(groups = {RequestBookingDto.NewBooking.class})
    LocalDateTime start;
    @Future(groups = {RequestBookingDto.NewBooking.class})
    @NotNull(groups = {RequestBookingDto.NewBooking.class})
    LocalDateTime end;

    public interface NewBooking {
    }
}