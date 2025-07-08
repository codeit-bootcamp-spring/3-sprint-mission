package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;


    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        log.debug("바이너리 컨텐츠 생성 시작 : fileName = {}, size = {}, contentType = {}", request.fileName(),
            request.bytes().length, request.contentType());

        String fileName = request.fileName();

        // File size
        byte[] bytes = request.bytes();

        String contentType = request.contentType();

        BinaryContent binaryContent = new BinaryContent(
            fileName,
            (long) bytes.length,
            contentType
        );

        binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(binaryContent.getId(), bytes);

        log.info("바이너리 콘텐츠 생성 완료 : id = {}, fileName = {}, size = {}", binaryContent.getId(),
            fileName, bytes.length);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID binaryContentId) {
        log.debug("바이너리 컨텐츠 조회 시작 : id = {}", binaryContentId);
        BinaryContentDto dto = binaryContentRepository.findById(binaryContentId)
            .map(binaryContentMapper::toDto)
            .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
        log.info("바이너리 컨텐츠 조회 완료 : id = {}, fileName = {}",
            dto.id(), dto.fileName());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.debug("바이너리 컨텐츠 목록 조회 시작 : ids = {}", binaryContentIds);
        List<BinaryContentDto> dtos = binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
        log.info("바이너리 컨텐츠 목록 조회 완료 : 조회된 항목 수 = {}", dtos.size());
        return dtos;
    }

    @Override
    public void delete(UUID binaryContentId) {
        log.debug("바이너리 콘텐츠 삭제 시작 : id = {}", binaryContentId);
        // 유효성
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw BinaryContentNotFoundException.withId(binaryContentId);
        }
        binaryContentRepository.deleteById(binaryContentId);
        log.info("바이너리 콘텐츠 삭제 완료 : id =  {}", binaryContentId);
    }
}