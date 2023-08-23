package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService service;

    @PostMapping
    public ResponseBookingDto add(@Validated(RequestBookingDto.NewBooking.class) @RequestBody RequestBookingDto requestBookingDto,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("User {} added booking", userId);
        return service.add(requestBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto accept(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(value = "approved") boolean approved) {
        log.info("User {} set status {} for booking {}", userId, approved, bookingId);
        return service.accept(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long bookingId) {
        log.info("User {} get booking {}", userId, bookingId);
        return service.get(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        log.info("User {} get all {} bookings", userId, state);
        return service.getAll(userId, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        log.info("User {} get {} bookings on his items", userId, state);
        return service.getAllByUser(userId, state);
    }
}
