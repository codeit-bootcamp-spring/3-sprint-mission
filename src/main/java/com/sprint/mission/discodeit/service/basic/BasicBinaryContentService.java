package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 관련 비즈니스 로직을 제공하는 서비스 클래스입니다.
 *
 * <p>생성, 조회, 다중 조회, 삭제 기능을 수행하며,
 * 스토리지 연동도 처리합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final ApplicationEventPublisher eventPublisher;

    private static final String SERVICE_NAME = "[BinaryContentService] ";

    /**
     * 바이너리 콘텐츠를 생성하고 저장소에 저장합니다.
     *
     * @param request 바이너리 콘텐츠 생성 요청
     * @return 생성된 바이너리 콘텐츠 DTO
     * @throws IOException 저장소 저장 중 오류 발생 시
     */
    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequest request) throws IOException {
        log.info(SERVICE_NAME + "바이너리 콘텐츠 생성 시도: fileName={}, contentType={}",
                request.fileName(), request.contentType());

        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        BinaryContent binaryContent = new BinaryContent(
                fileName,
                (long) bytes.length,
                contentType
        );

        binaryContentRepository.save(binaryContent);
        log.info(SERVICE_NAME + "BinaryContent 저장 성공");

        log.debug(SERVICE_NAME + "BinaryContent 생성 이벤트 발행");
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContent, bytes, Instant.now());
        eventPublisher.publishEvent(event);

        BinaryContentDto binaryContentDto = binaryContentMapper.toDto(binaryContent);
        log.info(SERVICE_NAME + "바이너리 콘텐츠 생성 성공: {}", binaryContentDto);

        return binaryContentDto;
    }

    /**
     * ID로 바이너리 콘텐츠를 조회합니다.
     *
     * @param binaryContentId 조회할 바이너리 콘텐츠 ID
     * @return 조회된 바이너리 콘텐츠 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID binaryContentId) {
        log.info(SERVICE_NAME + "바이너리 콘텐츠 조회 시도: id={}", binaryContentId);

        BinaryContentDto dto = binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toDto)
                .orElseThrow(() -> {
                    log.error(SERVICE_NAME + "바이너리컨텐츠 조회 실패 - 없음: id={}", binaryContentId);
                    return new BinaryContentNotFoundException(
                            "BinaryContent with id " + binaryContentId + " not found");
                });

        log.info(SERVICE_NAME + "바이너리 콘텐츠 조회 성공: {}", dto);
        return dto;
    }

    /**
     * 여러 ID로 바이너리 콘텐츠를 조회합니다.
     *
     * @param binaryContentIds 조회할 ID 목록
     * @return 조회된 바이너리 콘텐츠 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        log.info(SERVICE_NAME + "다중 바이너리 콘텐츠 조회 시도: ids={}", binaryContentIds);

        List<BinaryContent> list = binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
                .toList();

        log.info(SERVICE_NAME + "다중 바이너리 콘텐츠 조회 성공: 건수={}", list.size());
        return list;
    }

    /**
     * ID로 바이너리 콘텐츠를 삭제합니다.
     *
     * @param binaryContentId 삭제할 바이너리 콘텐츠 ID
     */
    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        log.info(SERVICE_NAME + "바이너리 콘텐츠 삭제 시도: id={}", binaryContentId);

        if (!binaryContentRepository.existsById(binaryContentId)) {
            log.error(SERVICE_NAME + "바이너리컨텐츠 삭제 실패 - 없음: id={}", binaryContentId);
            throw new BinaryContentNotFoundException(
                    SERVICE_NAME + "BinaryContent with id " + binaryContentId + " not found");
        }

        binaryContentRepository.deleteById(binaryContentId);

        log.info(SERVICE_NAME + "바이너리 콘텐츠 삭제 성공: id={}", binaryContentId);
    }

    @Override
    @Transactional
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {

        log.info(SERVICE_NAME + "BinaryContent 상태 업데이트 시도: id={}, status={}",
                binaryContentId, status);

        // 기존 엔티티를 조회
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> {
                    log.error(SERVICE_NAME + "BinaryContent 없음: id={}", binaryContentId);
                    return new BinaryContentNotFoundException(
                            "BinaryContent with id " + binaryContentId + " not found");
                });

        // 기존 엔티티의 상태만 업데이트
        try {
            binaryContent.updateStatus(status);
        } catch (Exception e) {
            log.error(SERVICE_NAME + "상태 업데이트 실패: id={}, status={}", binaryContentId, status, e);
        }

        // 더티 체킹으로 자동 업데이트되긴 하지만, 코드 가독성을 위해 추가
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        log.info(SERVICE_NAME + "BinaryContentStatus 업데이트 완료: id={}, status={}",
                binaryContentId, status);

        return binaryContentMapper.toDto(savedBinaryContent);
    }
}
