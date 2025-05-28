package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.Dto.binaryContent.FindBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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

    @Override
    public List<FindBinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
//        List<FindBinaryContentResponse> responses = new ArrayList<>();
//
//        if (binaryContentIds.isEmpty()) {
//            throw new RuntimeException("no ids in param");
//        }
//        List<BinaryContent> attachments = binaryContentRepository.findAllByIds(binaryContentIds);
//        if (attachments.isEmpty()) {
//            throw new RuntimeException("Not found all binaryContent by ids");
//        }
//
//        for (BinaryContent attachment : attachments) {
//            responses.add(new FindBinaryContentResponse(
//                    attachment.getId(),
//                    attachment.getCreatedAt(),
//                    attachment.getFileName(),
//                    attachment.getSize(),
//                    attachment.getContentType(),
//                    Base64.getEncoder().encodeToString(attachment.getBytes())
//            ));
//        }
//        return responses;
        return null;
    }

    @Override
    public FindBinaryContentResponse find(UUID binaryContentId) {
//        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId);
//        if (binaryContent == null)
//            throw new RuntimeException("BinaryContent with id " + binaryContentId + " not found");
////            return ResponseEntity.status(404).body("BinaryContent with id " + binaryContentId + " not found");
//
//        FindBinaryContentResponse response = new FindBinaryContentResponse(
//                binaryContent.getId(),
//                binaryContent.getCreatedAt(),
//                binaryContent.getFileName(),
//                binaryContent.getSize(),
//                binaryContent.getContentType(),
//                Base64.getEncoder().encodeToString(binaryContent.getBytes()));
//
//        return response;
        return null;
    }

    @Override
    public BinaryContentCreateResponse create(String fileName, Long size, String contentType, byte[] bytes, String extension) {
//        BinaryContent binaryContent = binaryContentRepository.createBinaryContent(fileName, size, contentType, bytes, extension);
//        return new BinaryContentCreateResponse(binaryContent.getId());
        return null;
    }

    @Override
    public void delete(UUID attachmentId) {
//        binaryContentRepository.deleteBinaryContentById(attachmentId); // file, jcf : throw exception
        return;
    }
}
