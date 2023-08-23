package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.booking.*;
import ru.practicum.shareit.exceptions.item.ItemAvailableException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public ResponseBookingDto add(RequestBookingDto requestBookingDto, long userId) {
        Item item = ItemMapper.toItem(itemService.get(requestBookingDto.getItemId(), userId));
        User booker = UserMapper.toUser(userService.getById(userId));
        Booking booking = new Booking();

        booking.setStart(requestBookingDto.getStart());
        booking.setEnd(requestBookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookStatus.WAITING);

        if (userId == item.getId()) {
            throw new BookingsUserException(String.format("User by id %d is an owner of item by id %d", userId, item.getId()));
        }
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new BookingTimeException("End must be after start");
        }
        if (!item.getAvailable()) {
            throw new ItemAvailableException(String.format("Item by id %d is not available", booking.getItem().getId()));
        }
        return BookingMapper.toResponseBookingDto(repository.save(booking));
    }

    @Override
    @Transactional
    public ResponseBookingDto accept(long bookingId, long userId, boolean approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Booking by id %d not found", bookingId)));
        if (userId != booking.getItem().getOwner().getId()) {
            throw new BookingsUserException(String.format("User by id %d is not related to this booking", userId));
        }
        if (booking.getStatus() != BookStatus.WAITING) {
            throw new BookingStatusException(String.format("Booking by id %d status already changed", bookingId));
        }
        if (approved) {
            booking.setStatus(BookStatus.APPROVED);
        } else {
            booking.setStatus(BookStatus.REJECTED);
        }
        return BookingMapper.toResponseBookingDto(repository.save(booking));
    }

    @Override
    @Transactional
    public ResponseBookingDto get(long userId, long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Booking by id %d not found", bookingId)));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toResponseBookingDto(booking);
        } else {
            throw new BookingsUserException(String.format("User by id %d is not related to this booking", userId));
        }
    }

    @Override
    @Transactional
    public List<ResponseBookingDto> getAll(long userId, String state) {
        userService.getById(userId);
            switch (state) {
                case "ALL":
                    return toResponseBookingDto(repository.findAllByBookerIdOrderByStartDesc(userId));
                case "PAST":
                    return toResponseBookingDto(repository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now()));
                case "FUTURE":
                    return toResponseBookingDto(repository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now()));
                case "CURRENT":
                    return toResponseBookingDto(repository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()));
                case "WAITING":
                    return toResponseBookingDto(repository.findAllByBookerIdAndStatusIsOrderByStartDesc(userId, BookStatus.WAITING));
                case "REJECTED":
                    return toResponseBookingDto(repository.findAllByBookerIdAndStatusIsOrderByStartDesc(userId, BookStatus.REJECTED));
            }
            throw new BookingStateException(String.format("Unknown state: %s", state));
    }

    @Override
    @Transactional
    public List<ResponseBookingDto> getAllByUser(long userId, String state) {
        userService.getById(userId);
            switch (state) {
                case "ALL":
                    return toResponseBookingDto(repository.findAllByOwnerId(userId));
                case "PAST":
                    return toResponseBookingDto(repository.findPastByOwnerId(userId, LocalDateTime.now()));
                case "FUTURE":
                    return toResponseBookingDto(repository.findFutureByOwnerId(userId, LocalDateTime.now()));
                case "CURRENT":
                    return toResponseBookingDto(repository.findCurrentByOwnerId(userId, LocalDateTime.now()));
                case "WAITING":
                    return toResponseBookingDto(repository.findWaitingByOwnerId(userId, BookStatus.WAITING));
                case "REJECTED":
                    return toResponseBookingDto(repository.findRejectedByOwnerId(userId, BookStatus.REJECTED));
            }
            throw new BookingStateException(String.format("Unknown state: %s", state));
    }

    private List<ResponseBookingDto> toResponseBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }
}
