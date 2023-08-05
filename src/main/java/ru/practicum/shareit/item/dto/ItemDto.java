package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemDto {
    long id;
    @NotBlank(groups = {NewItem.class})
    String name;
    @NotBlank(groups = {NewItem.class})
    String description;
    @NotNull(groups = {NewItem.class})
    @AssertTrue(groups = {NewItem.class})
    Boolean available;

    public interface NewItem {
    }
}
