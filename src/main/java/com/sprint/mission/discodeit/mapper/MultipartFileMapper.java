package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentData;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileMapper {

  public BinaryContentData toBinaryContentData(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    try {
      return new BinaryContentData(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getBytes());
    } catch (IOException e) {
      throw new BinaryContentProcessingException();
    }
  }

  public List<BinaryContentData> toBinaryContentDataList(List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      return List.of();
    }
    return files.stream()
        .filter(Objects::nonNull)
        .filter(f -> !f.isEmpty())
        .map(this::toBinaryContentData)
        .toList();
  }
}

