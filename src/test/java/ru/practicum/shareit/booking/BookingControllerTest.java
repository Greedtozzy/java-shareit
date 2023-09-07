package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.booking.BookingNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingService service;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final User user = new User(1, "name", "email@email.com");
    private final UserDto user1Dto = new UserDto(2, "name1", "email1@email.com");

    private final Item item = new Item(1, "item", "description",
            true, user, null,
            null, null, new ArrayList<>());
    private final ItemDto itemDto = new ItemDto(1, "item", "description",
            true, null,
            null, new ArrayList<>(), 0);
    private final RequestBookingDto requestBookingDto = new RequestBookingDto(1,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));
    private final ResponseBookingDto responseBookingDto = new ResponseBookingDto(1,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0),
            itemDto, user1Dto, BookStatus.WAITING);

    @Test
    void addTest() throws Exception {
        when(service.add(any(), anyLong()))
                .thenReturn(responseBookingDto);
        String response = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).add(requestBookingDto, 2);
        assertEquals(mapper.writeValueAsString(responseBookingDto), response);
    }

    @Test
    void addInvalidStartDateTest() throws Exception {
        RequestBookingDto invalidRequestBookingDto = new RequestBookingDto(1,
                LocalDateTime.of(2022, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidRequestBookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).add(invalidRequestBookingDto, 2);
    }

    @Test
    void addInvalidEndDateTest() throws Exception {
        RequestBookingDto invalidRequestBookingDto = new RequestBookingDto(1,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2022, 10, 30, 0, 0));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidRequestBookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).add(invalidRequestBookingDto, 2);
    }

    @Test
    void acceptTrueTest() throws Exception {
        ResponseBookingDto approvedDto = new ResponseBookingDto(1,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0),
                itemDto, user1Dto, BookStatus.APPROVED);
        when(service.accept(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(approvedDto);
        String response = mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).accept(1, 1, true);
        assertEquals(mapper.writeValueAsString(approvedDto), response);
    }

    @Test
    void acceptFalseTest() throws Exception {
        ResponseBookingDto rejectedDto = new ResponseBookingDto(1,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0),
                itemDto, user1Dto, BookStatus.REJECTED);
        when(service.accept(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(rejectedDto);
        String response = mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, false)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).accept(1, 1, false);
        assertEquals(mapper.writeValueAsString(rejectedDto), response);
    }

    @Test
    void acceptNotExistBookingTest() throws Exception {
        when(service.accept(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new BookingNotFoundException(String.format("Booking by id %d not found", 1)));
        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTest() throws Exception {
        when(service.get(anyLong(), anyLong()))
                .thenReturn(responseBookingDto);
        String response = mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).get(1, 1);
        assertEquals(mapper.writeValueAsString(responseBookingDto), response);
    }

    @Test
    void getNotExistBookingTest() throws Exception {
        when(service.get(anyLong(), anyLong()))
                .thenThrow(new BookingNotFoundException(String.format("Booking by id %d not found", 1)));
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest() throws Exception {
        when(service.getAll(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBookingDto));
        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getAll(1, "ALL", 0, 10);
        assertEquals(mapper.writeValueAsString(List.of(responseBookingDto)), response);
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(service.getAllByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBookingDto));
        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getAllByUser(1, "ALL", 0, 10);
        assertEquals(mapper.writeValueAsString(List.of(responseBookingDto)), response);
    }
}
