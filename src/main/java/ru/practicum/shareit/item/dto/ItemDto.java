package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    long id;
    @NotBlank(groups = {NewItem.class})
    String name;
    @NotBlank(groups = {NewItem.class})
    String description;
    @NotNull(groups = {NewItem.class})
    @AssertTrue(groups = {NewItem.class})
    Boolean available;
    ShortBookingDto lastBooking;
    ShortBookingDto nextBooking;
    List<ResponseCommentDto> comments;

    public interface NewItem {
    }
}
