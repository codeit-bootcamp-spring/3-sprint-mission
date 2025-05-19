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

    public BinaryContent create(BinaryContentCreateRequest binaryContentCreateDTO) {
//        UUID userId = userDTO.id();
        String fileName = binaryContentCreateDTO.fileName();
        byte[] bytes = binaryContentCreateDTO.bytes();
        String contentType = binaryContentCreateDTO.contentType();

        BinaryContent binaryContent = new BinaryContent(
                fileName,
                contentType,
                bytes
        );

        return binaryContentRepository.save(binaryContent);

    }

    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 파일이 존재하지 않습니다."));
    }

    public List<BinaryContent> findAll() {
        return binaryContentRepository.findAll();
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
