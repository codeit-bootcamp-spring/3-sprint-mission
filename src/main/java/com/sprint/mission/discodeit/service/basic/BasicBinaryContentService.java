package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        log.info("파일 업로드 요청: fileName={}, contentType={}, size={} bytes",
            request.fileName(), request.contentType(), request.bytes().length);

        BinaryContent binaryContent = new BinaryContent(
            request.fileName(),
            (long) request.bytes().length,
            request.contentType()
        );
        binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(
            binaryContent.getId(),
            request.bytes(),
            request.contentType()
        );

        log.info("파일 저장 완료: id={}", binaryContent.getId());
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        log.debug("파일 조회 요청: id={}", binaryContentId);

        return binaryContentRepository.findById(binaryContentId)
            .map(binaryContentMapper::toDto)
            .orElseThrow(() -> {
                log.warn("파일 조회 실패: 존재하지 않는 ID={}", binaryContentId);
                return new BinaryContentNotFoundException(binaryContentId);
            });
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.debug("다중 파일 조회 요청: {}개 ID", binaryContentIds.size());

        return binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        log.info("파일 삭제 요청: id={}", binaryContentId);

        if (!binaryContentRepository.existsById(binaryContentId)) {
            log.error("삭제 실패: 존재하지 않는 파일 id={}", binaryContentId);
            throw new BinaryContentNotFoundException(binaryContentId);
        }

        binaryContentRepository.deleteById(binaryContentId);
        log.info("파일 삭제 완료: id={}", binaryContentId);
    }
}