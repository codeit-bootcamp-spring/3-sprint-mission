package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContentResponse create(MultipartFile file) {
    BinaryContent newFile = BinaryContent.createBinaryContent(
        file.getName(), file.getSize(), file.getContentType()
    );

    BinaryContent newBinaryContent = binaryContentRepository.save(newFile);
    binaryContentStorage.put(newBinaryContent.getId(), convertToBytes(file));
    log.info(" User Profile 생성 : {}", newBinaryContent);
    return binaryContentMapper.entityToDto(newBinaryContent);
  }

  @Override
  public BinaryContentResponse findByIdOrThrow(UUID id) {
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
    return binaryContentMapper.entityToDto(binaryContent);
  }

  @Override
  public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllByIdIn(ids).stream()
        .map(binaryContentMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(UUID id) {
    if (!binaryContentRepository.existsById(id)) {
      throw new NoSuchElementException("BinaryContent with id " + id + " not found");
    }
    binaryContentRepository.deleteById(id);
  }

  private byte[] convertToBytes(MultipartFile file) {
    try {
      return file.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to convert file to byte array", e);
    }
  }
}
