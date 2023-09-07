package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.item.ItemNotFoundException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final BookingDto lastBookingDto = new BookingDto(1,
            LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0),
            2);
    private final BookingDto nextBookingDto = new BookingDto(2,
            LocalDateTime.of(2023, 10, 10, 0, 0),
            LocalDateTime.of(2023, 10, 20, 0, 0),
            2);
    private final ItemDto itemDto = new ItemDto(1, "item", "description",
            true, lastBookingDto,
            nextBookingDto, new ArrayList<>(), 1);
    private final CommentDto commentDto = new CommentDto(1, "text", "name1",
            LocalDateTime.of(2023, 8, 31, 0, 0));

    @Test
    void addTest() throws Exception {
        when(service.add(anyLong(), any()))
                .thenReturn(itemDto);
        String response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).add(1, itemDto);
        assertEquals(mapper.writeValueAsString(itemDto), response);
    }

    @Test
    void addWrongUserIdTest() throws Exception {
        when(service.add(anyLong(), any()))
                .thenThrow(new UserNotFoundException(String.format("User by id %d not found", 99)));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addWrongNameTest() throws Exception {
        ItemDto wrongNameDto = new ItemDto(1, "", "description",
                true, lastBookingDto,
                nextBookingDto, new ArrayList<>(), 1);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongNameDto)))
                .andExpect(status().isBadRequest());
        verify(service, never()).add(1, wrongNameDto);
    }

    @Test
    void addWrongDescriptionTest() throws Exception {
        ItemDto wrongDescriptionDto = new ItemDto(1, "item", "",
                true, lastBookingDto,
                nextBookingDto, new ArrayList<>(), 1);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongDescriptionDto)))
                .andExpect(status().isBadRequest());
        verify(service, never()).add(1, wrongDescriptionDto);
    }

    @Test
    void getAllTest() throws Exception {
        when(service.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(List.of(itemDto)), response);
    }

    @Test
    void getTest() throws Exception {
        when(service.get(anyLong(), anyLong()))
                .thenReturn(itemDto);
        String response = mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(itemDto), response);
    }

    @Test
    void getNotExistItemTest() throws Exception {
        when(service.get(anyLong(), anyLong()))
                .thenThrow(new ItemNotFoundException(String.format("Item by id %d not found", 99)));
        mvc.perform(get("/items/{itemId}", 99)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchTest() throws Exception {
        when(service.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));
        String response = mvc.perform(get("/items/search?text={text}", "item")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(List.of(itemDto)), response);
    }

    @Test
    void searchWrongTextTest() throws Exception {
        when(service.search(anyString(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        String response = mvc.perform(get("/items/search?text={text}", "")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(new ArrayList<>()), response);
    }

    @Test
    void updateTest() throws Exception {
        ItemDto updatedDto = new ItemDto(1, "upItem", "upDescription",
                true, lastBookingDto,
                nextBookingDto, new ArrayList<>(), 1);
        when(service.update(anyLong(), any(), anyLong()))
                .thenReturn(updatedDto);
        String response = mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(updatedDto), response);
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(service).delete(1, 1);
    }

    @Test
    void addCommentTest() throws Exception {
        when(service.addComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);
        String response = mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(commentDto), response);
    }
}
