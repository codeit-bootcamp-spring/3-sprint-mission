package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private final JpaBinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
        List<BinaryContentResponse> responses = new ArrayList<>();

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
    public BinaryContentResponse find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));

        return binaryContentMapper.toDto(binaryContent);
    }
}
