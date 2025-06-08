package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;


  @Override
  public BinaryContent create(BinaryContentCreateRequest createRequest) {
    try {
      BinaryContent binaryContent = new BinaryContent(
          createRequest.fileName(),
          (long) createRequest.bytes().length,
          createRequest.contentType()
      );

      BinaryContent createdBinaryContent = this.binaryContentRepository.save(binaryContent);
      binaryContentStorage.put(binaryContent.getId(), createRequest.bytes());

      return createdBinaryContent;

    } catch (Exception e) {
      System.err.println("❌ create() 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
      throw e; // 다시 던져야 정상 동작
    }

  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    return this.binaryContentRepository
        .findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException(
            "binaryContent with id " + binaryContentId + " not found"));
  }


  // Reference : https://www.baeldung.com/java-filter-collection-by-list
  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return this.binaryContentRepository.findAllById(binaryContentIds).stream()
        .map(binaryContentMapper::toDto).toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    if (!this.binaryContentRepository.existsById(binaryContentId)) {
      throw new NoSuchElementException("binaryContent with id " + binaryContentId + " not found");
    }

    this.binaryContentRepository.deleteById(binaryContentId);
  }
}
