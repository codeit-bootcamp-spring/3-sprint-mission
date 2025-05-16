package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private static final Logger logger = Logger.getLogger(BasicBinaryContentService.class.getName());

    private final BinaryContentRepository binaryContentRepository;

    // create
    @Override
    public BinaryContent createBinaryContent(BinaryContentCreateRequest request) {

        String fileName = request.fileName();
        String cotentType = request.contentType();
        byte[] data = request.data();


        BinaryContent binaryContent = new BinaryContent(fileName, cotentType, data);

        return binaryContentRepository.saveBinaryContent(binaryContent);
    }

    // find
    @Override
    public BinaryContent findBinaryContentById(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당하는 바이너리 컨텐츠가 없습니다."));
    }

    // findAll
    @Override
    public List<BinaryContent> findAllBinaryContent(List<UUID> ids) {
        return binaryContentRepository.findAllByIds(ids).stream()
                .toList();
    }

    // delete
    @Override
    public boolean deleteBinaryContent(UUID id) {
        return binaryContentRepository.deleteById(id);
    }
}
