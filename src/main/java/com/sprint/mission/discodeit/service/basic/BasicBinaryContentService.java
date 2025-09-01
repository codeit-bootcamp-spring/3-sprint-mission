package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.jpa.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicBinaryContentService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Service("basicBinaryContentService")
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        List<BinaryContentDto> responses = new ArrayList<>();

        if (binaryContentIds.isEmpty()) {
            throw new RuntimeException("no ids in param");
        }
        List<BinaryContent> attachments = binaryContentRepository.findAllByIdIn(binaryContentIds);

        if (attachments.isEmpty()) {
            throw new RuntimeException("Not found all binaryContent by ids");
        }

        for (BinaryContent attachment : attachments) {
            responses.add(binaryContentMapper.toDto(attachment));
        }
        return responses;

    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public BinaryContentDto updatedStatus(UUID binaryContentId, BinaryContentStatus status) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));
        binaryContent.updateStatus(status);
        binaryContentRepository.save(binaryContent);
        return binaryContentMapper.toDto(binaryContent);
    }
}
