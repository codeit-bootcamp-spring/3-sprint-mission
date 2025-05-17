package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContent create(BinaryContentCreateRequest binaryContentCreateRequest) {
        BinaryContent binaryContent =  BinaryContent.of(binaryContentCreateRequest.getFileName());
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    public BinaryContent find(UUID id) {
        return binaryContentRepository.loadById(id);
    }

    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.loadAll().stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .toList();
    }

    public void delete(UUID id) {
        binaryContentRepository.delete(id);
    }
}
