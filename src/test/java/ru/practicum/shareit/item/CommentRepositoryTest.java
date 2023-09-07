package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Comment comment;
    private Item item;
    private User user;
    private User user1;

    @BeforeEach
    void create() {
        user = userRepository.save(new User(1, "name", "email@email.com"));
        user1 = userRepository.save(new User(2, "name1", "email1@email.com"));
        item = itemRepository.save(new Item(1, "item", "description",
                true, user, null,
                null, null, null));
        comment = commentRepository.save(new Comment(1, "text", item, user1, LocalDateTime.now()));
    }

    @Test
    void findAllByItemIdTest() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertEquals(List.of(comment), comments);
        assertEquals(comments.size(), 1);
    }
}
