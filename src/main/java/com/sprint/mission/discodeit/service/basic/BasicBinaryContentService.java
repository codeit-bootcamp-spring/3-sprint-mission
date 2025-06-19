package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    // 1. 메타정보만으로 BinaryContent 엔티티 생성 및 저장
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType);

    BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

    // 2. 실제 바이너리 데이터는 별도 저장소에 저장
    binaryContentStorage.put(savedBinaryContent.getId(), bytes);

    return savedBinaryContent;
  }

  @Override
  public BinaryContent createFromOptional(Optional<BinaryContentCreateRequest> optionalRequest) {
    return optionalRequest.map(this::create).orElse(null);
  }

  @Override
  public List<BinaryContent> createAll(List<BinaryContentCreateRequest> requests) {
    return requests.stream()
        .map(this::create)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContent find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new CustomException.BinaryContentNotFoundException(
            "BinaryContent with id " + binaryContentId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds);
  }

  @Override
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new CustomException.BinaryContentNotFoundException(
          "BinaryContent with id " + binaryContentId + " not found");
    }

    // 데이터베이스에서 BinaryContent 삭제
    binaryContentRepository.deleteById(binaryContentId);
  }
}
