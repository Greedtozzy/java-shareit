package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Get all users");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Get user by id {}", id);
        return service.getById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public UserDto add(@Validated(UserDto.NewUser.class) @RequestBody UserDto userDto) {
        log.info("User {} added", userDto);
        return service.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody UserDto userDto,
                          @PathVariable long id) {
        log.info("User by id {} updated", id);
        return service.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        service.deleteById(id);
        log.info("User by id {} deleted", id);
    }
}