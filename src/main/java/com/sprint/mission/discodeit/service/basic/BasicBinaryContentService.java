package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.notfound.NotFoundBinaryContentException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("basicBinaryContentService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public BinaryContentResponseDto findById(UUID id) {
    BinaryContent foundBinaryContent = binaryContentRepository.findById(id)
        .orElseThrow(NotFoundBinaryContentException::new);

    return binaryContentMapper.toDto(foundBinaryContent);
  }

  @Override
  public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllByIdIn(ids).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }
}
