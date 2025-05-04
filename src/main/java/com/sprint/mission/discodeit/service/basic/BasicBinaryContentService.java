package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.dto.BinaryContent.BinaryContentCreateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public Optional<BinaryContent> find(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new RuntimeException("해당 ID는 존재하지 않습니다.");
        }
        return binaryContentRepository.findById(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids);
    }

    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new RuntimeException("해당 ID는 존재하지 않습니다.");
        }
        binaryContentRepository.deleteById(id);
    }
}