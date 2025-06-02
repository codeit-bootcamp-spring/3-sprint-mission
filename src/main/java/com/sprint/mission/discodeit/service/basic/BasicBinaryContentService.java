package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage storage;

    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) throws IOException {
        BinaryContent meta = new BinaryContent(
                request.fileName(),
                (long) request.bytes().length,
                request.contentType()
        );
        BinaryContent saved = binaryContentRepository.save(meta);

        storage.put(saved.getId(), request.bytes());

        return binaryContentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID id) {
        BinaryContent meta = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID는 존재하지 않습니다."));
        return binaryContentMapper.toDto(meta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Override
    public void delete(UUID id) throws IOException {
        BinaryContent meta = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID는 존재하지 않습니다."));
        binaryContentRepository.delete(meta);
        storage.put(id, new byte[0]);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> download(UUID id) throws IOException {
        BinaryContent meta = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID는 존재하지 않습니다."));
        BinaryContentDto dto = binaryContentMapper.toDto(meta);
        return storage.download(dto);
    }
}