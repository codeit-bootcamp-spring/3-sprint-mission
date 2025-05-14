package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  public BasicBinaryContentService(BinaryContentRepository binaryContentRepository) {
    this.binaryContentRepository = binaryContentRepository;
  }

  @Override
  public BinaryContent create(BinaryContentCreateRequest request, UUID userId, UUID messageId) {
    if (!request.isValid()) {
      throw new IllegalArgumentException("유효하지 않은 파일 정보입니다.");
    }

    BinaryContent content = (messageId != null)
        ? new BinaryContent(request.fileName(), userId, messageId) // 메시지 첨부파일
        : new BinaryContent(request.fileName(), userId); // 사용자 프로필 이미지

    return binaryContentRepository.save(content);
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return binaryContentRepository.find(id);
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) { // id목록으로 조회 List<UUID> ids = List.of(id1, id2);
    if (ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("ID 리스트가 비어있거나 null입니다."); // 조회 결과 없음 -> 빈 리스트를 리턴 or exception throw
    }

    return ids.stream()
        .map(binaryContentRepository::find) // 각 Id에 대해 Optional<BinaryContent> 반환
        .flatMap(Optional::stream) // 존재하는 것만 뽑아낸다.
        .toList(); // List<>로
  }

  @Override
  public void delete(UUID id) {
    binaryContentRepository.delete(id);
  }
}
