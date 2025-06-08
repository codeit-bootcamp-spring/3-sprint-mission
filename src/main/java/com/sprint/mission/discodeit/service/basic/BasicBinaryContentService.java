package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequest request, UUID userId,
        UUID messageId) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 파일 정보입니다.");
        }

        String fileName = request.fileName();
        Long size = request.size();
        String contentType = request.contentType();

        BinaryContent newFile = binaryContentRepository.save(
            new BinaryContent(fileName, size, contentType));

        binaryContentStorage.put(newFile.getId(), request.bytes());

        return binaryContentMapper.toDto(newFile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BinaryContentDto> findById(UUID id) {
        return binaryContentRepository.findById(id)
            .map(binaryContentMapper::toDto);
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(
        List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException(
                "ID 리스트가 비어있거나 null입니다.");
        }

        return ids
            .stream()
            .map(binaryContentRepository::findById)
            .flatMap(Optional::stream)
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
