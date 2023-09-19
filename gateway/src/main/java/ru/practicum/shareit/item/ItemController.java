package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Validated(ItemDto.NewItem.class) @RequestBody ItemDto itemDto) {
        log.info("Item add by user {}", userId);
        return client.add(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable long itemId,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item by id {}", itemId);
        return client.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                         @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        log.info("Get all items");
        return client.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text") String text,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                         @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        log.info("Search item by text {}", text);
        return client.search(text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemDto itemDto,
                                         @PathVariable long itemId) {
        log.info("Update item {}", itemId);
        return client.update(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId) {
        log.info("Delete item {}", itemId);
        return client.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Validated(CommentDto.NewComment.class) @RequestBody CommentDto comment) {
        log.info("Add comment from user {} to item {}", userId, itemId);
        return client.addComment(userId, itemId, comment);
    }
}