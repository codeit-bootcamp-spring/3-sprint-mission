package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring", uses = {BinaryContentMapper.class}
)
public interface MessageAttachmentMapper {

  @Mapping(target = "id", expression = "java(attachment.getId().getAttachmentId())")
  @Mapping(target = "fileName", expression = "java(attachment.getAttachment().getFileName())")
  @Mapping(target = "contentType", expression = "java(attachment.getAttachment().getContentType())")
  @Mapping(target = "size", expression = "java(attachment.getAttachment().getSize())")
  BinaryContentResponse toResponse(MessageAttachment attachment);
}