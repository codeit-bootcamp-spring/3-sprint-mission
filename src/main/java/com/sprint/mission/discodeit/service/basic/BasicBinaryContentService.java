package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final ApplicationEventPublisher eventPublisher;

    // create
    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequest request) {

        String fileName = request.fileName();
        String contentType = request.contentType();
        byte[] bytes = request.bytes();

        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);

        // 이벤트 객체를 생성
        BinaryContentCreatedEvent binaryContentEvent = BinaryContentCreatedEvent.now(
            binaryContent.getId(),
            bytes
        );
        // 생성된 이벤트 객체를 전달인자로 이벤트 발행
        eventPublisher.publishEvent(binaryContentEvent);

        binaryContentRepository.save(binaryContent);

        return binaryContentMapper.toDto(binaryContent);
    }

    // find
    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(id));

        return binaryContentMapper.toDto(binaryContent);
    }

    // findAll
    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    // update
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {

        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));

        binaryContent.updateStatus(status);
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        BinaryContentDto dto = binaryContentMapper.toDto(savedBinaryContent);

        eventPublisher.publishEvent(new BinaryContentUpdatedEvent(savedBinaryContent.getId(), dto));

        return dto;
    }

    // delete
    @Override
    @Transactional
    public void delete(UUID id) {
        binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(id));

        binaryContentRepository.deleteById(id);
    }
}
