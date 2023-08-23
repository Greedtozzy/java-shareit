package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(long userId, BookStatus status);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 order by b.start desc")
    List<Booking> findAllByOwnerId(long userId);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findPastByOwnerId(long userId, LocalDateTime now);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findFutureByOwnerId(long userId, LocalDateTime now);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc")
    List<Booking> findCurrentByOwnerId(long userId, LocalDateTime now);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findWaitingByOwnerId(long userId, BookStatus status);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findRejectedByOwnerId(long userId, BookStatus status);

    List<Booking> findAllByBookerIdAndItemIdAndStatusIsAndStartIsBefore(long userId, long itemId, BookStatus status, LocalDateTime now);

    List<Booking> findAllByItemIdAndStatusNot(long itemId, BookStatus status);
}
