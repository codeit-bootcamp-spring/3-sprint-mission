package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public BinaryContentDTO create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    BinaryContent binaryContent =
        BinaryContent.builder()
            .fileName(fileName)
            .contentType(contentType)
            .size((long) bytes.length)
            .build();

    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);

    return binaryContentMapper.toDTO(binaryContent);

  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDTO find(UUID id) {
    return binaryContentRepository.findById(id)
        .map(binaryContentMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 파일이 존재하지 않습니다."));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDTO> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllById(ids).stream()
        .map(binaryContentMapper::toDTO)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    if (!binaryContentRepository.existsById(id)) {
      throw new NoSuchElementException("해당 파일이 존재하지 않습니다.");
    }
    binaryContentRepository.deleteById(id);
  }
}
