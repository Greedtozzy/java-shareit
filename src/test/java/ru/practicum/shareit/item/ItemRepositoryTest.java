package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private Item item;
    private User user;
    private User user1;
    private ItemRequest request;
    private final LocalDateTime now = LocalDateTime.now();
    private final Pageable pageable = PageRequest.of(0, 10);


    @BeforeEach
    void create() {
        user = userRepository.save(new User(1, "name", "email@email.com"));
        user1 = userRepository.save(new User(2, "name1", "email1@email.com"));
        request = requestRepository.save(new ItemRequest(1, "description", user1, now));
        item = itemRepository.save(new Item(1, "item", "description",
                true, user, request,
                null, null, null));
    }

    @AfterEach
    void delete() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdTest() {
        List<Item> items = itemRepository.findAllByOwnerId(user.getId(), pageable);
        assertEquals(List.of(item), items);
        assertEquals(items.size(), 1);
    }

    @Test
    void searchTest() {
        List<Item> items = itemRepository.search("item", pageable);
        assertEquals(List.of(item), items);
        assertEquals(items.size(), 1);
    }

    @Test
    void findAllByRequestIdTest() {
        List<Item> items = itemRepository.findAllByRequestId(request.getId());
        assertEquals(List.of(item), items);
        assertEquals(items.size(), 1);
    }
}
