package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.FileNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadInvalidException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        log.info("[binaryContent] 업로드 요청: fileName={}, contentType={}, size={} bytes",
            fileName, contentType, bytes != null ? bytes.length : 0);

        if (bytes == null || bytes.length == 0) {
            log.warn("[binaryContent] 업로드 실패 - 파일 없음 또는 0바이트: fileName={}", fileName);
            throw new FileUploadInvalidException(fileName);
        }

        // 1. 메타데이터 저장(status = PROCESSING)
        BinaryContent binaryContent = new BinaryContent(
            fileName,
            request.size(),
            contentType
        );
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        // 2. dto 변환 (status 포함)
        BinaryContentDto dto = binaryContentMapper.toDto(savedBinaryContent);

        // 3. 이벤트 발행(커밋 후 업로드)
        String objectKey = savedBinaryContent.getId().toString();
        eventPublisher.publishEvent(new BinaryContentCreatedEvent(
            dto,
            objectKey,
            request.toInputStreamSupplier()
        ));

        log.info("[binaryContent] 저장 완료: id={}, size={} bytes", binaryContent.getId(),
            bytes.length);

        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentDto find(UUID id) {
        log.info("[binaryContent] 조회 요청: id={}", id);

        return binaryContentRepository.findById(id)
            .map(binaryContentMapper::toDto)
            .orElseThrow(() -> {
                log.warn("[binaryContent] 조회 실패 - 존재하지 않는 id: id={}", id);
                return new FileNotFoundException(id);
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.info("[binaryContent] 전체 조회 요청: size={}", binaryContentIds.size());

        return binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            log.warn("[binaryContent] 삭제 실패 - 존재하지 않는 id: id={}", id);
            throw new FileNotFoundException(id);
        }
        binaryContentRepository.deleteById(id);
        log.info("[binaryContent] 삭제 완료: id={}", id);
    }
}
