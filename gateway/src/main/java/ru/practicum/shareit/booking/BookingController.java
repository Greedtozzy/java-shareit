package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Validated(RequestBookingDto.NewBooking.class) @RequestBody RequestBookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return client.add(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return client.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return client.getAll(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(value = "state", defaultValue = "ALL", required = false) String stateParam,
                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                               @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("");
        return client.getAllByUser(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> accept(@PathVariable long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(value = "approved") boolean approved) {
        log.info("");
        return client.accept(bookingId, userId, approved);
    }
}
