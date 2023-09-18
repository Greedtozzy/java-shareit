package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Request add from user {}", userId);
        return client.add(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long requestId) {
        log.info("Get request by id {}", requestId);
        return client.getById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                      @Positive @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        log.info("Get requests from user {}", userId);
        return client.get(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                         @Positive @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        log.info("Get all requests by user {}", userId);
        return client.getAll(userId, from, size);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Object> delete(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        log.info("Delete request {}", requestId);
        return client.delete(userId, requestId);
    }
}
