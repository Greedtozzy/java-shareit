package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private User user1;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest1;

    private final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));

    @BeforeEach
    void create() {
        user = userRepository.save(new User(1, "name", "email@email.com"));
        user1 = userRepository.save(new User(2, "name1", "email1@email.com"));
        itemRequest = requestRepository.save(new ItemRequest(1, "description", user, LocalDateTime.of(2023, 9, 1, 0, 0)));
        itemRequest1 = requestRepository.save(new ItemRequest(2, "description1", user1, LocalDateTime.of(2023, 9, 1, 0, 0)));
    }

    @AfterEach
    void delete() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestorIdTest() {
        List<ItemRequest> requestsByRequestorId = requestRepository.findAllByRequestorId(user.getId(), pageable);
        assertEquals(List.of(itemRequest), requestsByRequestorId);
        assertEquals(requestsByRequestorId.size(), 1);
    }

    @Test
    void findAllByRequestorIdIsNot() {
        List<ItemRequest> requestsByRequestorIdIsNot = requestRepository.findAllByRequestorIdIsNot(user.getId(), pageable);
        assertEquals(List.of(itemRequest1), requestsByRequestorIdIsNot);
        assertEquals(requestsByRequestorIdIsNot.size(), 1);
    }
}