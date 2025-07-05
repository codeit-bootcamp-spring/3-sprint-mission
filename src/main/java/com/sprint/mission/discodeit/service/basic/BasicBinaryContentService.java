package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.vo.BinaryContentData;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContent create(BinaryContentData binaryContentData) {
    if (binaryContentData.bytes() == null || binaryContentData.fileName() == null
        || binaryContentData.contentType() == null) {
      throw BinaryContentException.invalidRequest();
    }

    BinaryContent binaryContent = BinaryContent.create(
        binaryContentData.fileName(),
        (long) binaryContentData.bytes().length,
        binaryContentData.contentType(),
        binaryContentData.bytes()
    );

    return binaryContentRepository.save(binaryContent);
  }

  @Override
  public BinaryContent find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(
            () -> BinaryContentException.notFound(binaryContentId));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentIds.stream()
        .map(this::find)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    binaryContentRepository.delete(id);
  }
}
