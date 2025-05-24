package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.notfound.NotFoundBinaryContentException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicBinaryContentService")
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContent create(BinaryContentDTO binaryContentDTO) {
    BinaryContent binaryContent = BinaryContentDTO.toEntity(binaryContentDTO);

    binaryContentRepository.save(binaryContent);

    return binaryContent;
  }

  @Override
  public BinaryContentResponseDTO findById(UUID id) {
    BinaryContent foundBinaryContent = binaryContentRepository.findById(id)
        .orElseThrow(NotFoundBinaryContentException::new);

    return BinaryContent.toDTO(foundBinaryContent);
  }

  @Override
  public List<BinaryContentResponseDTO> findAll() {
    return binaryContentRepository.findAll().stream()
        .map(BinaryContent::toDTO)
        .toList();
  }

  @Override
  public List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllByIdIn(ids).stream()
        .map(BinaryContent::toDTO)
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    binaryContentRepository.deleteById(id);
  }
}
