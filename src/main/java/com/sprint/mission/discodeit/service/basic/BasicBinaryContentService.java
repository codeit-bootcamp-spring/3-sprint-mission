package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest binaryContentCreateRequest) {
        BinaryContent binaryContent =  BinaryContent.of(binaryContentCreateRequest.getFileName(), binaryContentCreateRequest.getFilePath());
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent find(@RequestParam("binaryContentId") UUID id) {
        return binaryContentRepository.loadById(id);
    }

//    public BinaryContent findByUserId(UUID id) { return binaryContentRepository.loadByUserId(id); }

    @Override
    public List<BinaryContent> findAll() { return binaryContentRepository.loadAll(); }

    @Override
    public void delete(UUID id) { binaryContentRepository.delete(id); }
}