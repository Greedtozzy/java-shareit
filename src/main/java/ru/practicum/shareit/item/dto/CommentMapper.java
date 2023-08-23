package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
    public static ResponseCommentDto toResponseCommentDto(Comment comment) {
        ResponseCommentDto dto = new ResponseCommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }
}
