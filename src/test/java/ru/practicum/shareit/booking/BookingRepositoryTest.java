package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private User user1;
    private Item item;
    private Booking booking;
    private final LocalDateTime now = LocalDateTime.now();
    private final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));

    @BeforeEach
    void createUserAndItem() {
        user = userRepository.save(new User(1, "name", "email@email.com"));
        user1 = userRepository.save(new User(2, "name1", "email1@email.com"));
        item = itemRepository.save(new Item(1, "item", "description",
                true, user, null,
                null, null, null));
        booking = bookingRepository.save(new Booking(1,
                now.minusDays(1),
                now.plusDays(1),
                item, user1, BookStatus.WAITING));
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(user1.getId(), pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(user1.getId(), now.plusDays(2), pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findAllByBookerIdAndStartIsAfterTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(user1.getId(), now.minusDays(2), pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(user1.getId(), now, now, pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findAllByBookerIdAndStatusIsTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusIs(user1.getId(), BookStatus.WAITING, pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findAllByOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findAllByOwnerId(user.getId(), pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findPastByOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findPastByOwnerId(user.getId(), now.plusDays(2), pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findFutureByOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findFutureByOwnerId(user.getId(), now.minusDays(2), pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findCurrentByOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findCurrentByOwnerId(user.getId(), now, pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findWaitingByOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findWaitingByOwnerId(user.getId(), BookStatus.WAITING, pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findRejectedByOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findRejectedByOwnerId(user.getId(), BookStatus.WAITING, pageable);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusIsAndStartIsBeforeTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusIsAndStartIsBefore(user1.getId(), item.getId(), BookStatus.WAITING, now);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findAllByItemIdAndStatusNotTest() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusNot(item.getId(), BookStatus.REJECTED);
        assertEquals(List.of(booking), bookings);
        assertEquals(bookings.size(), 1);
    }
}
