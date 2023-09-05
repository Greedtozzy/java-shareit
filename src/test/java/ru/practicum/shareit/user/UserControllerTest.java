package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final UserDto dto = new UserDto(1, "name", "email@email.com");

    @Test
    void addUserTest() throws Exception {
        when(service.add(any()))
                .thenReturn(dto);
        String response = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).add(dto);
        assertEquals(mapper.writeValueAsString(dto), response);
    }

    @Test
    void addUserInvalidEmailTest() throws Exception {
        UserDto invalidEmailDto = new UserDto(1, "name", "email.email.com");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidEmailDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).add(invalidEmailDto);
    }

    @Test
    void addUserInvalidNameTest() throws Exception {
        UserDto invalidNameDto = new UserDto(1, "", "email@email.com");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidNameDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).add(invalidNameDto);
    }

    @Test
    void getAllTest() throws Exception {
        when(service.getAll())
                .thenReturn(List.of(dto));
        String response = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(List.of(dto)), response);
    }

    @Test
    void getByIdTest() throws Exception {
        when(service.getById(anyLong()))
                .thenReturn(dto);
        String response = mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(service.getById(1)), response);
    }

    @Test
    void getByIdNotExistUserTest() throws Exception {
        when(service.getById(anyLong()))
                .thenThrow(new UserNotFoundException(String.format("User by id %d not found", 99)));
        mvc.perform(get("/users/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTest() throws Exception {
        UserDto updatedDto = new UserDto(1, "upName", "upEmail@email.com");
        when(service.update(any(), anyLong()))
                .thenReturn(updatedDto);
        String response = mvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).update(updatedDto, 1);
        assertEquals(mapper.writeValueAsString(updatedDto), response);

    }

    @Test
    void updateInvalidEmailTest() throws Exception {
        UserDto invalidUpdatedDto = new UserDto(1, "name", "email.email.com");
        mvc.perform(patch("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidUpdatedDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).update(invalidUpdatedDto, 1);
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
        verify(service).deleteById(1);
    }
}
