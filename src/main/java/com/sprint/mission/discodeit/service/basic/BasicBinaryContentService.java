package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    if (request.bytes() == null || request.fileName() == null || request.contentType() == null) {
      throw BinaryContentException.invalidRequest();
    }

    BinaryContent binaryContent = BinaryContent.create(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType(),
        request.bytes()
    );

    return binaryContentRepository.save(binaryContent);
  }

  @Override
  public BinaryContent find(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(
            () -> BinaryContentException.notFound(id));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return ids.stream()
        .map(this::find)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    binaryContentRepository.delete(id);
  }
}
