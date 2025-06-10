package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    //
    private final BinaryContentMapper binaryContentMapper;

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public BinaryContent create(BinaryContentCreateRequest request) throws IOException {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();
        BinaryContent binaryContent = new BinaryContent(
            fileName,
            (long) bytes.length,
            contentType
        );
        binaryContentStorage.put(binaryContent.getId(), request.bytes()); // storage에 저장

        return binaryContentRepository.save(binaryContent);
    }

    @Override
    @Transactional(
        readOnly = true,
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRES_NEW,
        isolation = Isolation.READ_COMMITTED)
    public BinaryContent find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException(
                "BinaryContent with id " + binaryContentId + " not found"));
    }

    @Override
    @Transactional(readOnly = true,
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
            .toList();
    }

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException(
                "BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }
}
