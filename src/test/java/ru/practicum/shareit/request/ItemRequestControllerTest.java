package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.request.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description",
            LocalDateTime.of(2023, 8, 30, 0, 0), new ArrayList<>());

    @Test
    void addTest() throws Exception {
        when(service.add(anyLong(), any()))
                .thenReturn(itemRequestDto);
        String response = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).add(1, itemRequestDto);
        assertEquals(mapper.writeValueAsString(itemRequestDto), response);
    }

    @Test
    void addInvalidRequestTest() throws Exception {
        ItemRequestDto invalidDto = new ItemRequestDto(1, "",
                LocalDateTime.of(2023, 8, 30, 0, 0), new ArrayList<>());
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTest() throws Exception {
        when(service.get(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));
        String response = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).get(1, 0, 20);
        assertEquals(mapper.writeValueAsString(List.of(itemRequestDto)), response);
    }

    @Test
    void getNotExistItemTest() throws Exception {
        when(service.get(anyLong(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        String response = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).get(1, 0, 20);
        assertEquals(mapper.writeValueAsString(new ArrayList<>()), response);
    }

    @Test
    void getAllTest() throws Exception {
        when(service.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));
        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getAll(1, 0, 20);
        assertEquals(mapper.writeValueAsString(List.of(itemRequestDto)), response);
    }

    @Test
    void getAllNotExistTest() throws Exception {
        when(service.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getAll(1, 0, 20);
        assertEquals(mapper.writeValueAsString(new ArrayList<>()), response);
    }

    @Test
    void getByIdTest() throws Exception {
        when(service.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);
        String response = mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getById(1, 1);
        assertEquals(mapper.writeValueAsString(itemRequestDto), response);
    }

    @Test
    void getByIdNotExistTest() throws Exception {
        when(service.getById(anyLong(), anyLong()))
                .thenThrow(new ItemRequestNotFoundException(String.format("Request by id %d not found", 1)));
        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(service).delete(1, 1);
    }
}
