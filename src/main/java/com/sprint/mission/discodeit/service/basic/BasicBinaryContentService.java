package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.vo.BinaryContentData;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContentResponse create(BinaryContentData binaryContentData) {
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

    BinaryContent saved = binaryContentRepository.save(binaryContent);

    return BinaryContentResponse.from(saved);
  }

  @Override
  public BinaryContentResponse find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .map(BinaryContentResponse::from)
        .orElseThrow(() -> BinaryContentException.notFound(binaryContentId));
  }

  @Override
  public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentIds.stream()
        .map(this::find)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    binaryContentRepository.deleteById(id);
  }
}
