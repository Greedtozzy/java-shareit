package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Add request {}", requestDto);
        return service.add(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                    @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        log.info("Get list all requests by user {}", userId);
        return service.get(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                       @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        log.info("Get list all requests");
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long requestId) {
        log.info("Get request by id {}", requestId);
        return service.getById(userId, requestId);
    }

    @DeleteMapping("/{requestId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long requestId) {
        log.info("Delete request by id {}", requestId);
        service.delete(userId, requestId);
    }
}
