package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.request.ItemRequestNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;

    private final User user = new User(1, "name", "email@email.com");
    private final UserDto userDto = new UserDto(1, "name", "email@email.com");
    private final UserDto userDto1 = new UserDto(2, "name1", "email1@email.com");
    private final ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.of(2023, 9, 1, 0, 0));
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description", LocalDateTime.of(2023, 9, 1, 0, 0), new ArrayList<>());

    @Test
    void addTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        assertEquals(itemRequestService.add(1, itemRequestDto), itemRequestDto);
    }

    @Test
    void getTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        when(itemRequestRepository.findAllByRequestorId(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        assertEquals(itemRequestService.get(1, 0, 10), List.of(itemRequestDto));
    }

    @Test
    void getAllTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userDto1);
        when(itemRequestRepository.findAllByRequestorIdIsNot(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        assertEquals(itemRequestService.getAll(2, 0, 10), List.of(itemRequestDto));
    }

    @Test
    void getByIdTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        assertEquals(itemRequestService.getById(1, 1), itemRequestDto);
    }

    @Test
    void getByWrongIdTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getById(1, 1));
        assertEquals(e.getMessage(), "Request by id 1 not found");
    }

    @Test
    void deleteTest() {
        when(itemRequestRepository.findById(anyLong()))
                        .thenReturn(Optional.of(itemRequest));
        itemRequestService.delete(1, 1);
        Mockito.verify(itemRequestRepository).deleteById(1L);
    }
}
