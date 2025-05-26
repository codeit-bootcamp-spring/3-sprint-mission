package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContent create(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        BinaryContent binaryContent =
                BinaryContent.builder()
                        .fileName(fileName)
                        .contentType(contentType)
                        .content(bytes)
                        .size((long) bytes.length)
                        .build();

        return binaryContentRepository.save(binaryContent);

    }

    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 파일이 존재하지 않습니다."));
    }

    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .toList();
    }

    public void delete(UUID id) {
        if(!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("해당 파일이 존재하지 않습니다.");
        }
        binaryContentRepository.deleteById(id);
    }
}
