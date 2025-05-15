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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;


    @Override
    public BinaryContent create(BinaryContentCreateRequest binaryContentCreateRequest) {
        // File size
        byte[] size = binaryContentCreateRequest.getFileData();

        BinaryContent binaryContent = new BinaryContent(
                binaryContentCreateRequest.getFileName(),
                binaryContentCreateRequest.getFileData(),
                binaryContentCreateRequest.getFileType(),
                size.length
        );
        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        // 유효성
        if (!binaryContentRepository.existsById(id)) {
            throw new IllegalArgumentException("BinaryContent with id " + id + " not found");
        }
        binaryContentRepository.deleteById(id);
    }
}