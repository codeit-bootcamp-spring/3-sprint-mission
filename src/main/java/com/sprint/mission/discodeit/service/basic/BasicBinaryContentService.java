package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.annotation.Logging;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Logging
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    private final BinaryContentMapper binaryContentMapper;

    /**
     * 주어진 요청 DTO를 기반으로 BinaryContent 생성
     *
     * @param createRequest 생성 요청 DTO
     * @return 생성된 BinaryContent
     */
    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequest createRequest) {

        BinaryContent binaryContent = BinaryContent.builder()
                .fileName(createRequest.fileName())
                .contentType(createRequest.contentType())
                .size(((long) createRequest.bytes().length))
                .build();

        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), createRequest.bytes());

        return binaryContentMapper.toDto(binaryContent);
    }

    /**
     * 주어진 ID 목록에 해당하는 BinaryContent 전체 조회
     *
     * @param ids 조회할 BinaryContent ID 목록
     * @return 조회된 BinaryContent 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("조회할 ID 목록이 비어 있습니다.");
        }

        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(ids);

        return binaryContents.stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    /**
     * 주어진 id에 해당하는 BinaryContent 조회
     *
     * @param id 조회할 BinaryContent ID
     * @return 조회된 BinaryContent
     */
    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 컨텐츠입니다."));

        return binaryContentMapper.toDto(binaryContent);
    }

    /**
     * 주어진 id에 해당하는 BinaryContent 삭제
     *
     * @param id 삭제할 BinaryContent id
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(("존재하지 않는 컨텐츠입니다.")));

        binaryContentRepository.delete(binaryContent);
    }
}
