package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.exceptions.booking.*;
import ru.practicum.shareit.exceptions.item.ItemAvailableException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;

    private final User user = new User(1, "name", "email@email.com");
    private final User user1 = new User(2, "name1", "email1@email.com");
    private final UserDto userDto = new UserDto(1, "name", "email@email.com");
    private final UserDto user1Dto = new UserDto(2, "name1", "email1@email.com");

    private final Item item = new Item(1, "item", "description",
            true, user, null,
            null, null, new ArrayList<>());
    private final ItemDto itemDto = new ItemDto(1, "item", "description",
            true, null,
            null, new ArrayList<>(), 0);
    private final ItemDto itemDtoNotAvailable = new ItemDto(1, "item", "description",
            false, null,
            null, new ArrayList<>(), 0);
    private final Booking booking = new Booking(1,
            LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0),
            item, user1, BookStatus.WAITING);
    private final Booking bookingApproved = new Booking(1,
            LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0),
            item, user1, BookStatus.APPROVED);
    private final RequestBookingDto requestBookingDto = new RequestBookingDto(1,
            LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0));
    private final RequestBookingDto requestBookingDtoWrongDate = new RequestBookingDto(1,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 8, 30, 0, 0));
    private final ResponseBookingDto responseBookingDto = new ResponseBookingDto(1,
            LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0),
            itemDto, user1Dto, BookStatus.WAITING);
    private final ResponseBookingDto responseBookingDtoApproved = new ResponseBookingDto(1,
            LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0),
            itemDto, user1Dto, BookStatus.APPROVED);

    @Test
    void addTest() {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(itemDto);
        when(userService.getById(anyLong()))
                .thenReturn(user1Dto);
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        assertEquals(bookingService.add(requestBookingDto, 2), responseBookingDto);
    }

    @Test
    void addByOwnerTest() {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(itemDto);
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        Exception e = assertThrows(BookingsUserException.class, () -> bookingService.add(requestBookingDto, 1));
        assertEquals(e.getMessage(), "User by id 1 is an owner of item by id 1");
    }

    @Test
    void addByItemIsNotAvailableTest() {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(itemDtoNotAvailable);
        when(userService.getById(anyLong()))
                .thenReturn(user1Dto);
        Exception e = assertThrows(ItemAvailableException.class, () -> bookingService.add(requestBookingDto, 2));
        assertEquals(e.getMessage(), "Item by id 1 is not available");
    }

    @Test
    void addWithWrongStartEndTest() {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(itemDto);
        when(userService.getById(anyLong()))
                .thenReturn(user1Dto);
        Exception e = assertThrows(BookingTimeException.class, () -> bookingService.add(requestBookingDtoWrongDate, 2));
        assertEquals(e.getMessage(), "End must be after start");
    }

    @Test
    void acceptTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(bookingApproved);
        assertEquals(bookingService.accept(1, 1, true), responseBookingDtoApproved);
    }

    @Test
    void acceptByWrongUserTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Exception e = assertThrows(BookingsUserException.class, () -> bookingService.accept(1, 2, true));
        assertEquals(e.getMessage(), "User by id 2 is not related to this booking");
    }

    @Test
    void acceptedWhenStatusNotWaiting() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingApproved));
        Exception e = assertThrows(BookingStatusException.class, () -> bookingService.accept(1, 1, true));
        assertEquals(e.getMessage(), "Booking by id 1 status already changed");
    }

    @Test
    void getTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertEquals(bookingService.get(1, 1), responseBookingDto);
    }

    @Test
    void getWrongBookingIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(BookingNotFoundException.class, () -> bookingService.get(1, 99));
        assertEquals(e.getMessage(), "Booking by id 99 not found");
    }

    @Test
    void getWrongUserIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Exception e = assertThrows(BookingsUserException.class, () -> bookingService.get(99, 1));
        assertEquals(e.getMessage(), "User by id 99 is not related to this booking");
    }

    @Test
    void getAllStateAllTest() {
        when(bookingRepository.findAllByBookerId(anyLong(), any()))
                .thenReturn(List.of(booking));
        assertEquals(bookingService.getAll(1, "ALL", 0, 10), List.of(responseBookingDto));
    }

    @Test
    void getAllStatePastTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = new Booking(1,
                now.minusDays(2),
                now.minusDays(1),
                item, user1, BookStatus.APPROVED);
        ResponseBookingDto pastResponseBookingDto = new ResponseBookingDto(1,
                now.minusDays(2),
                now.minusDays(1),
                itemDto, user1Dto, BookStatus.APPROVED);
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(List.of(pastBooking));
        assertEquals(bookingService.getAll(1, "PAST", 0, 10), List.of(pastResponseBookingDto));
    }

    @Test
    void getAllStateFutureTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking futureBooking = new Booking(1,
                now.plusDays(1),
                now.plusDays(2),
                item, user1, BookStatus.APPROVED);
        ResponseBookingDto futureResponseBookingDto = new ResponseBookingDto(1,
                now.plusDays(1),
                now.plusDays(2),
                itemDto, user1Dto, BookStatus.APPROVED);
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(List.of(futureBooking));
        assertEquals(bookingService.getAll(1, "FUTURE", 0, 10), List.of(futureResponseBookingDto));
    }

    @Test
    void getAllStateCurrentTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking currentBooking = new Booking(1,
                now.minusDays(1),
                now.plusDays(1),
                item, user1, BookStatus.APPROVED);
        ResponseBookingDto currentResponseBookingDto = new ResponseBookingDto(1,
                now.minusDays(1),
                now.plusDays(1),
                itemDto, user1Dto, BookStatus.APPROVED);
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(currentBooking));
        assertEquals(bookingService.getAll(1, "CURRENT", 0, 10), List.of(currentResponseBookingDto));
    }

    @Test
    void getAllStateWaitingTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking waitingBooking = new Booking(1,
                now.minusDays(1),
                now.minusDays(2),
                item, user1, BookStatus.WAITING);
        ResponseBookingDto waitingResponseBookingDto = new ResponseBookingDto(1,
                now.minusDays(1),
                now.minusDays(2),
                itemDto, user1Dto, BookStatus.WAITING);
        when(bookingRepository.findAllByBookerIdAndStatusIs(anyLong(), any(), any()))
                .thenReturn(List.of(waitingBooking));
        assertEquals(bookingService.getAll(1, "WAITING", 0, 10), List.of(waitingResponseBookingDto));
    }

    @Test
    void getAllStateRejectedTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking rejectedBooking = new Booking(1,
                now.minusDays(1),
                now.minusDays(2),
                item, user1, BookStatus.REJECTED);
        ResponseBookingDto rejectedResponseBookingDto = new ResponseBookingDto(1,
                now.minusDays(1),
                now.minusDays(2),
                itemDto, user1Dto, BookStatus.REJECTED);
        when(bookingRepository.findAllByBookerIdAndStatusIs(anyLong(), any(), any()))
                .thenReturn(List.of(rejectedBooking));
        assertEquals(bookingService.getAll(1, "REJECTED", 0, 10), List.of(rejectedResponseBookingDto));
    }

    @Test
    void getAllWrongState() {
        Exception e = assertThrows(BookingStateException.class, () -> bookingService.getAll(1, "blabla", 0, 10));
        assertEquals(e.getMessage(), "Unknown state: blabla");
    }

    @Test
    void getAllByUserStateAllTest() {
        when(bookingRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(booking));
        assertEquals(bookingService.getAllByUser(1, "ALL", 0, 10), List.of(responseBookingDto));
    }

    @Test
    void getAllByUserStatePastTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = new Booking(1,
                now.minusDays(1),
                now.minusDays(2),
                item, user1, BookStatus.APPROVED);
        ResponseBookingDto pastResponseBookingDto = new ResponseBookingDto(1,
                now.minusDays(1),
                now.minusDays(2),
                itemDto, user1Dto, BookStatus.APPROVED);
        when(bookingRepository.findPastByOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(pastBooking));
        assertEquals(bookingService.getAllByUser(1, "PAST", 0, 10), List.of(pastResponseBookingDto));
    }

    @Test
    void getAllByUserStateFutureTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking futureBooking = new Booking(1,
                now.plusDays(1),
                now.plusDays(2),
                item, user1, BookStatus.APPROVED);
        ResponseBookingDto futureResponseBookingDto = new ResponseBookingDto(1,
                now.plusDays(1),
                now.plusDays(2),
                itemDto, user1Dto, BookStatus.APPROVED);
        when(bookingRepository.findFutureByOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(futureBooking));
        assertEquals(bookingService.getAllByUser(1, "FUTURE", 0, 10), List.of(futureResponseBookingDto));
    }

    @Test
    void getAllByUserStateCurrentTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking currentBooking = new Booking(1,
                now.minusDays(1),
                now.plusDays(1),
                item, user1, BookStatus.APPROVED);
        ResponseBookingDto currentResponseBookingDto = new ResponseBookingDto(1,
                now.minusDays(1),
                now.plusDays(1),
                itemDto, user1Dto, BookStatus.APPROVED);
        when(bookingRepository.findCurrentByOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(currentBooking));
        assertEquals(bookingService.getAllByUser(1, "CURRENT", 0, 10), List.of(currentResponseBookingDto));
    }

    @Test
    void getAllByUserStateWaitingTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking waitingBooking = new Booking(1,
                now.minusDays(2),
                now.minusDays(1),
                item, user1, BookStatus.WAITING);
        ResponseBookingDto waitingResponseBookingDto = new ResponseBookingDto(1,
                now.minusDays(2),
                now.minusDays(1),
                itemDto, user1Dto, BookStatus.WAITING);
        when(bookingRepository.findWaitingByOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(waitingBooking));
        assertEquals(bookingService.getAllByUser(1, "WAITING", 0, 10), List.of(waitingResponseBookingDto));
    }

    @Test
    void getAllByUserStateRejectedTest() {
        LocalDateTime now = LocalDateTime.now();
        Booking rejectedBooking = new Booking(1,
                now.minusDays(2),
                now.minusDays(1),
                item, user1, BookStatus.REJECTED);
        ResponseBookingDto rejectedResponseBookingDto = new ResponseBookingDto(1,
                now.minusDays(2),
                now.minusDays(1),
                itemDto, user1Dto, BookStatus.REJECTED);
        when(bookingRepository.findRejectedByOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(rejectedBooking));
        assertEquals(bookingService.getAllByUser(1, "REJECTED", 0, 10), List.of(rejectedResponseBookingDto));
    }

    @Test
    void getAllByUserWrongState() {
        Exception e = assertThrows(BookingStateException.class, () -> bookingService.getAllByUser(1, "blabla", 0, 10));
        assertEquals(e.getMessage(), "Unknown state: blabla");
    }

    @Test
    void pageableTest() {
        Exception e = assertThrows(PaginationException.class, () -> bookingService.getAll(1, "ALL", -1, -1));
        assertEquals(e.getMessage(), "From must be positive or zero, size must be positive.");
    }
}
