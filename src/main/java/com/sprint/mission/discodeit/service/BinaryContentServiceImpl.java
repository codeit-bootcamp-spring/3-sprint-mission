package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    BinaryContent binaryContent;
    if (request.contentType() == BinaryContent.ContentType.PROFILE_IMAGE) {
      binaryContent = BinaryContent.createProfileImage(
          request.data(),
          request.fileName(),
          request.mimeType(),
          request.userId()
      );
    } else if (request.contentType() == BinaryContent.ContentType.MESSAGE_ATTACHMENT) {
      binaryContent = BinaryContent.createMessageAttachment(
          request.data(),
          request.fileName(),
          request.mimeType(),
          request.messageId()
      );
    } else {
      throw new IllegalArgumentException("지원하지 않는 BinaryContent 타입입니다: " + request.contentType());
    }
    return binaryContentRepository.save(binaryContent);
  }

  @Override
  public BinaryContent find(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(
            () -> new IllegalArgumentException("BinaryContent를 찾을 수 없습니다. [id: " + id + "]"));
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
