package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentInvalidException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequest request, UUID userId,
                                   UUID messageId) {
        if (!request.isValid()) {
            throw BinaryContentInvalidException.withFile(request.fileName());
        }

        String fileName = request.fileName();
        Long size = request.size();
        String contentType = request.contentType();

        BinaryContent newFile = binaryContentRepository.save(
                new BinaryContent(fileName, size, contentType));

        applicationEventPublisher.publishEvent(
                new BinaryContentCreatedEvent(newFile.getId(), request.bytes()));

        return binaryContentMapper.toDto(newFile);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto findById(UUID id) {
        return binaryContentRepository.findById(id)
                .map(binaryContentMapper::toDto)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(id));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(
            List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw BinaryContentInvalidException.missingFile();
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

    @Override
    @Transactional
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
        binaryContent.updateStatus(status);
        binaryContentRepository.save(binaryContent);
        return binaryContentMapper.toDto(binaryContent);
    }
}
