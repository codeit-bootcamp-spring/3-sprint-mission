package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest binaryContentCreateRequest) {
        String fileName = binaryContentCreateRequest.fileName();
        byte[] bytes = binaryContentCreateRequest.bytes();
        String contentType = binaryContentCreateRequest.contentType();

        BinaryContent binaryContent =  BinaryContent.of(
                fileName,
                (long) bytes.length,
                contentType,
                bytes
        );
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.loadById(binaryContentId);
        if (binaryContent == null) {
            throw new IllegalArgumentException("[BinaryContent] 유효하지 않은 binaryContent. (binaryContentId=" + binaryContentId + ")");
        }

        return binaryContent;
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.loadAllByIdIn(binaryContentIds).stream()
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (binaryContentRepository.loadById(binaryContentId) == null) {
            throw new NoSuchElementException("[BinaryContent] 유효하지 않은 binaryContent (binaryContentId=" + binaryContentId + ")");
        }
        binaryContentRepository.delete(binaryContentId);
    }
}