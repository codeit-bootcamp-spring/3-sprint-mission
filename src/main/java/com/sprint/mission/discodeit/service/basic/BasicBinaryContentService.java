package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        log.info("파일 업로드 시작 - 파일명: {}, 크기: {} bytes, 타입: {}", fileName, bytes.length, contentType);

        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), bytes);

        log.info("파일 업로드 완료 - fileId: {}, 파일명: {}, 크기: {} bytes", binaryContent.getId(), fileName,
            bytes.length);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId).map(binaryContentMapper::toDto)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto).toList();
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        log.info("파일 삭제 시작 - fileId: {}", binaryContentId);

        if (!binaryContentRepository.existsById(binaryContentId)) {
            log.warn("파일 삭제 실패 - 존재하지 않는 파일 ID: {}", binaryContentId);
            throw new BinaryContentNotFoundException(binaryContentId);
        }
        binaryContentRepository.deleteById(binaryContentId);

        log.info("파일 삭제 완료 - fileId: {}", binaryContentId);

    }
}
