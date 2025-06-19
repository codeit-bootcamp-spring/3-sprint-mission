package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
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
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequest request) throws IOException {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();
        BinaryContent binaryContent = new BinaryContent(
            fileName,
            (long) bytes.length,
            contentType
        );
        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), bytes); // storage에 저장

        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional
    public BinaryContentDto find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toDto)
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
                    "[BinaryContentService] BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }
}
