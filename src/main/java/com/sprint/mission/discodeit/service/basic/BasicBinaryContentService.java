package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.global.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
            request.fileName(),
            (long) request.bytes().length,
            request.contentType()
        );

        BinaryContent saved = binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(saved.getId(), request.bytes());

        return binaryContentMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BinaryContentDto> find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
            .map(binaryContentMapper::toDto);
    }


    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
            .map(binaryContentMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void update(UUID binaryContentId, BinaryContentCreateRequest request) {
        BinaryContent content = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId.toString()));

        content.updateContent((long) request.bytes().length, request.contentType());
        binaryContentStorage.put(binaryContentId, request.bytes());
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        binaryContentStorage.delete(binaryContentId);
        binaryContentRepository.deleteById(binaryContentId);
    }


}
