package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    binaryContentStorage.put(binaryContent.getId(), bytes);
    binaryContentRepository.save(binaryContent);
    BinaryContentDto dto = binaryContentMapper.toDto(binaryContent);
    dto.setBytes(bytes);
    return dto;
  }

  @Transactional
  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    try {
      BinaryContentDto binaryContentDto = binaryContentRepository
          .findById(binaryContentId)
          .map(binaryContentMapper::toDto).orElseThrow(() -> new NoSuchElementException(
              "BinaryContent with id " + binaryContentId + " not found"));
      InputStream inputStream = binaryContentStorage.get(binaryContentId);
      byte[] data = inputStream.readAllBytes();
      binaryContentDto.setBytes(data);
      inputStream.close();
      System.out.println("binaryContentDto.getBytes() = " + binaryContentDto.getBytes());
      return binaryContentDto;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    List<BinaryContentDto> binaryContentDtos = new ArrayList<>();
    binaryContentRepository.findAllByIdIn(binaryContentIds)
        .forEach(binaryContent -> {
          try {
            BinaryContentDto binaryContentDto = binaryContentMapper.toDto(binaryContent);
            InputStream inputStream = binaryContentStorage.get(binaryContent.getId());
            byte[] data = inputStream.readAllBytes();
            binaryContentDto.setBytes(data);
            binaryContentDtos.add(binaryContentDto);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
    return binaryContentDtos;
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
    }
    binaryContentRepository.deleteById(binaryContentId);
  }

  @Transactional
  @Override
  public ResponseEntity<?> download(UUID binaryContentId) {
    BinaryContentDto binaryContentDto = binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto).orElseThrow(NoSuchElementException::new);
    try {
      byte[] bytes = binaryContentStorage.get(binaryContentId).readAllBytes();
      binaryContentDto.setBytes(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return binaryContentStorage.download(binaryContentDto);
  }
}
