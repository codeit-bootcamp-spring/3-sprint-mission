package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;


  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {

    String fileName = request.fileName();

    // File size
    byte[] bytes = request.bytes();

    String contentType = request.contentType();

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType,
        bytes
    );
    return binaryContentRepository.save(binaryContent);
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public BinaryContent find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(
            () -> new NoSuchElementException(
                "BinaryContent with id " + binaryContentId + " not found"));
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
        .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    // 유효성
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new IllegalArgumentException("BinaryContent with id " + binaryContentId + " not found");
    }
    binaryContentRepository.deleteById(binaryContentId);
  }
}