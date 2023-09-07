package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookState;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.PaginationException;
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

        if (userId == item.getId()) {
            throw new BookingsUserException(String.format("User by id %d is an owner of item by id %d", userId, item.getId()));
        }
        if (!requestBookingDto.getStart().isBefore(requestBookingDto.getEnd())) {
            throw new BookingTimeException("End must be after start");
        }
        if (!item.getAvailable()) {
            throw new ItemAvailableException(String.format("Item by id %d is not available", item.getId()));
        }

        Booking booking = new Booking();
        booking.setStart(requestBookingDto.getStart());
        booking.setEnd(requestBookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookStatus.WAITING);

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
    public List<ResponseBookingDto> getAll(long userId, String state, int from, int size) {
        if (from < 0 || size < 1) {
            throw new PaginationException("From must be positive or zero, size must be positive.");
        }
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (toState(state)) {
            case ALL:
                return toResponseBookingDto(repository.findAllByBookerId(userId, pageable));
            case PAST:
                return toResponseBookingDto(repository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable));
            case FUTURE:
                return toResponseBookingDto(repository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),pageable));
            case CURRENT:
                return toResponseBookingDto(repository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable));
            case WAITING:
                return toResponseBookingDto(repository.findAllByBookerIdAndStatusIs(userId, BookStatus.WAITING, pageable));
            case REJECTED:
                return toResponseBookingDto(repository.findAllByBookerIdAndStatusIs(userId, BookStatus.REJECTED, pageable));
        }
        throw new BookingStateException(String.format("Unknown state: %s", state));
    }

    @Override
    @Transactional
    public List<ResponseBookingDto> getAllByUser(long userId, String state, int from, int size) {
        if (from < 0 || size < 1) {
            throw new PaginationException("From must be positive or zero, size must be positive.");
        }
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (toState(state)) {
            case ALL:
                return toResponseBookingDto(repository.findAllByOwnerId(userId, pageable));
            case PAST:
                return toResponseBookingDto(repository.findPastByOwnerId(userId, LocalDateTime.now(), pageable));
            case FUTURE:
                return toResponseBookingDto(repository.findFutureByOwnerId(userId, LocalDateTime.now(), pageable));
            case CURRENT:
                return toResponseBookingDto(repository.findCurrentByOwnerId(userId, LocalDateTime.now(), pageable));
            case WAITING:
                return toResponseBookingDto(repository.findWaitingByOwnerId(userId, BookStatus.WAITING, pageable));
            case REJECTED:
                return toResponseBookingDto(repository.findRejectedByOwnerId(userId, BookStatus.REJECTED, pageable));
        }
        throw new BookingStateException(String.format("Unknown state: %s", state));
    }

    private List<ResponseBookingDto> toResponseBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    private BookState toState(String state) {
        try {
            return BookState.valueOf(state);
        } catch (Exception e) {
            throw new BookingStateException(String.format("Unknown state: %s", state));
        }
    }
}
