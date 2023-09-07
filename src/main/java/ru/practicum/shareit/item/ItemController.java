package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService service;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        log.info("Get all items by user id {}", userId);
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable long itemId,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item by item id {}", itemId);
        return service.get(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(value = "text") String text,
                                @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        log.info("Search items by text {}", text);
        return service.search(text, from, size);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Validated(ItemDto.NewItem.class) @RequestBody ItemDto itemDto) {
        log.info("User {} add item {}", userId, itemDto);
        return service.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        log.info("User {} updated item by id {}", userId, itemId);
        return service.update(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId) {
        log.info("User {} deleted item by id {}", userId, itemId);
        service.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Validated(CommentDto.NewComment.class) @RequestBody CommentDto comment) {
        log.info("User {} add comment to item {}", userId, itemId);
        return service.addComment(comment, userId, itemId);
    }
}
