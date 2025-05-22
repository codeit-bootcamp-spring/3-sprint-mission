package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  public final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    if (request.bytes() == null || request.bytes().length == 0) {
      throw new RuntimeException(
          ErrorMessages.format("binaryContent", ErrorMessages.ERROR_FILE_UPLOAD_INVALID));
    }

    BinaryContent file = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType(),
        request.bytes()
    );

    return binaryContentRepository.save(file);
  }

  @Override
  public BinaryContent find(UUID id) {
    return binaryContentRepository.find(id)
        .orElseThrow(() -> new NoSuchElementException(
            ErrorMessages.format("BinaryContent", ErrorMessages.ERROR_NOT_FOUND)
        ));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return ids.stream()
        .map(this::find)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    if (!binaryContentRepository.existsById(id)) {
      throw new IllegalArgumentException(
          ErrorMessages.format("binaryContent", ErrorMessages.ERROR_EXISTS));
    }
    binaryContentRepository.deleteById(id);
  }
}
