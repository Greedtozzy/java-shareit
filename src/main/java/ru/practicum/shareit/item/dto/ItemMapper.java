package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getLastBooking() != null) itemDto.setLastBooking(BookingMapper.toShortBookingDto(item.getLastBooking()));
        if (item.getNextBooking() != null) itemDto.setNextBooking(BookingMapper.toShortBookingDto(item.getNextBooking()));
        if (item.getComments() != null) {
            itemDto.setComments(item.getComments().stream().map(CommentMapper::toResponseCommentDto).collect(Collectors.toList()));
        } else {
            itemDto.setComments(new ArrayList<>());
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ShortItemDto toShortItemDto(Item item) {
        return new ShortItemDto(item.getId(), item.getName());
    }
}
