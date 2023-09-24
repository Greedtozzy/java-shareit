package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RequestBookingDto {
	long itemId;
	@FutureOrPresent(groups = {RequestBookingDto.NewBooking.class})
	@NotNull(groups = {RequestBookingDto.NewBooking.class})
	LocalDateTime start;
	@Future(groups = {RequestBookingDto.NewBooking.class})
	@NotNull(groups = {RequestBookingDto.NewBooking.class})
	LocalDateTime end;

	public interface NewBooking {
	}
}
