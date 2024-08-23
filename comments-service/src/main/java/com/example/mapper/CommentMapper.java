package com.example.mapper;

import com.example.dto.CommentDto;
import com.example.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto commentToCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    Comment commentDtoToComment(CommentDto commentDto);
}
