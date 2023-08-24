package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long userId, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime now, LocalDateTime now1, Sort sort);

    List<Booking> findAllByBookerIdAndStatusIs(long userId, BookStatus status, Sort sort);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1")
    List<Booking> findAllByOwnerId(long userId, Sort sort);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.end < ?2")
    List<Booking> findPastByOwnerId(long userId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.start > ?2")
    List<Booking> findFutureByOwnerId(long userId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findCurrentByOwnerId(long userId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.status = ?2")
    List<Booking> findWaitingByOwnerId(long userId, BookStatus status, Sort sort);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.status = ?2")
    List<Booking> findRejectedByOwnerId(long userId, BookStatus status, Sort sort);

    List<Booking> findAllByBookerIdAndItemIdAndStatusIsAndStartIsBefore(long userId, long itemId, BookStatus status, LocalDateTime now);

    List<Booking> findAllByItemIdAndStatusNot(long itemId, BookStatus status);
}
