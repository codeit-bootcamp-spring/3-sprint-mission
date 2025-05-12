package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    /**
     * 주어진 요청 DTO를 기반으로 BinaryContent 생성 및 저장
     *
     * @param createRequestDTO 생성 요청 DTO
     * @return 생성된 BinaryContent
     */
    @Override
    public BinaryContent create(BinaryContentCreateRequestDTO createRequestDTO) {
        String fileName = createRequestDTO.fileName();
        byte[] content = createRequestDTO.content();
        String contentType = createRequestDTO.contentType();

        // BinaryContent 생성
        BinaryContent binaryContent = BinaryContent.builder()
                .fileName(fileName)
                .size((long) content.length)
                .contentType(contentType)
                .content(content)
                .build();

        // 데이터 저장
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    /**
     * 주어진 ID 목록에 해당하는 BinaryContent 전체 조회
     *
     * @param ids 조회할 BinaryContent ID 목록
     * @return 조회된 BinaryContent 리스트
     */
    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(id -> binaryContentRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 BinaryContent입니다.")))
                .toList();
    }

    /**
     *주어진 id에 해당하는 BinaryContent 조회
     *
     * @param id 조회할 BinaryContent ID
     * @return 조회된 BinaryContent
     * @throws NoSuchElementException 해당 ID의 BinaryContent가 존재하지 않는 경우
     */
    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 BinaryContent입니다."));
    }

    /**
     * 주어진 id에 해당하는 BinaryContent 삭제
     *
     * @param id 삭제할 대상 BinaryContent id
     */
    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
