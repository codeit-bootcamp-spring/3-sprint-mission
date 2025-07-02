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
        log.info("[BasicBinaryContentService] Creating binary content. [fileName={}]",
            request.fileName());

        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        BinaryContent binaryContent = new BinaryContent(
            fileName,
            (long) bytes.length,
            contentType
        );

        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), bytes);

        log.debug("[BasicBinaryContentService] Binary content created. [id={}] [size={}]",
            binaryContent.getId(), bytes.length);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        log.info("[BasicBinaryContentService] Finding binary content. [id={}]", binaryContentId);

        return binaryContentRepository.findById(binaryContentId)
            .map(binaryContent -> {
                log.debug("[BasicBinaryContentService] Binary content found. [id={}]",
                    binaryContentId);
                return binaryContentMapper.toDto(binaryContent);
            })
            .orElseThrow(() -> {
                log.warn("[BasicBinaryContentService] Binary content not found. [id={}]",
                    binaryContentId);
                return new BinaryContentNotFoundException(binaryContentId);
            });
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.info("[BasicBinaryContentService] Finding multiple binary contents. [ids={}]",
            binaryContentIds);

        List<BinaryContentDto> result = binaryContentRepository.findAllById(binaryContentIds)
            .stream()
            .map(binaryContentMapper::toDto)
            .toList();

        log.debug("[BasicBinaryContentService] Binary contents found. [count={}]", result.size());
        return result;
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        log.info("[BasicBinaryContentService] Deleting binary content. [id={}]", binaryContentId);

        if (!binaryContentRepository.existsById(binaryContentId)) {
            log.warn("[BasicBinaryContentService] Cannot delete - content not found. [id={}]",
                binaryContentId);
            throw new BinaryContentNotFoundException(binaryContentId);
        }

        binaryContentRepository.deleteById(binaryContentId);
        log.debug("[BasicBinaryContentService] Binary content deleted. [id={}]", binaryContentId);
    }
}
