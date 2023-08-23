package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public static ResponseBookingDto toResponseBookingDto(Booking booking) {
        ResponseBookingDto dto = new ResponseBookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(ItemMapper.toShortItemDto(booking.getItem()));
        dto.setBooker(UserMapper.toShortUserDto(booking.getBooker()));
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public static ShortBookingDto toShortBookingDto(Booking booking) {
        return new ShortBookingDto(booking.getId(), booking.getBooker().getId());
    }
}
