package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentWithBytes;
import com.sprint.mission.discodeit.Dto.binaryContent.FindBinaryContentResponse;
import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicBinaryContentService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Service("basicBinaryContentService")
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final JpaBinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public List<JpaBinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
        List<JpaBinaryContentResponse> responses = new ArrayList<>();

        if (binaryContentIds.isEmpty()) {
            throw new RuntimeException("no ids in param");
        }
        List<BinaryContent> attachments = binaryContentRepository.findAllByIdIn(binaryContentIds);

        if (attachments.isEmpty()) {
            throw new RuntimeException("Not found all binaryContent by ids");
        }

        for (BinaryContent attachment : attachments) {
            responses.add(binaryContentMapper.toDto(attachment));
        }
        return responses;

    }

    @Override
    public JpaBinaryContentResponse find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));

        return binaryContentMapper.toDto(binaryContent);
    }


    @Override
    @Transactional(readOnly = true)
    public BinaryContentWithBytes download(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElse(null);

        InputStream inputStream = binaryContentStorage.get(binaryContentId);
        byte[] bytes;
        try {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BinaryContentWithBytes attachment = null;

        if (binaryContent != null) {
            attachment = new BinaryContentWithBytes(
                    binaryContent.getFileName(),
                    binaryContent.getSize(),
                    binaryContent.getContentType(),
                    binaryContent.getExtension(),
                    bytes
            );
        }

        return attachment;
    }


    @Override
    public BinaryContentCreateResponse create(String fileName, Long size, String contentType, byte[] bytes, String extension) {
//        BinaryContent binaryContent = binaryContentRepository.createBinaryContent(fileName, size, contentType, bytes, extension);
//        return new BinaryContentCreateResponse(binaryContent.getId());
        return null;
    }
}
