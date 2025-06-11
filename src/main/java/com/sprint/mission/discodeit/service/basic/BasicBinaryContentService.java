package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    public final BinaryContentRepository binaryContentRepository;
    public final BinaryContentStorage binaryContentStorage;
    public final BinaryContentMapper binaryContentMapper;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        byte[] bytes = request.bytes();
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException(
                ErrorMessages.format("binaryContent", ErrorMessages.ERROR_FILE_UPLOAD_INVALID));
        }

        //1. 메타데이터만 가진 BinaryContent 객체 생성
        BinaryContent file = new BinaryContent(
            request.fileName(),
            (long) bytes.length,
            request.contentType()
        );

        //2. 실제 바이너리 데이터 저장소에 따로 저장
        binaryContentStorage.put(file.getId(), bytes);

        BinaryContent savedBinaryContent = binaryContentRepository.save(file);

        return binaryContentMapper.toDto(savedBinaryContent);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("BinaryContent", ErrorMessages.ERROR_NOT_FOUND)
            ));

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
            .map(this::find)
            .filter(Objects::nonNull)
            .toList();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new IllegalArgumentException(
                ErrorMessages.format("binaryContent", ErrorMessages.ERROR_EXISTS));
        }
        binaryContentRepository.deleteById(id);
    }
}
