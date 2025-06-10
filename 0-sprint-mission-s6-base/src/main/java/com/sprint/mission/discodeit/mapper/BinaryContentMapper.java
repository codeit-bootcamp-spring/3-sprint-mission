package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {

  public BinaryContentResponse entityToDto(BinaryContent binaryContent) {
    if (binaryContent == null) {
      return null;
    }
    return BinaryContentResponse.builder()
        .id(binaryContent.getId())
        .fileName(binaryContent.getFileName())
        .size(binaryContent.getSize())
        .contentType(binaryContent.getContentType())
        .build();
  }

}
