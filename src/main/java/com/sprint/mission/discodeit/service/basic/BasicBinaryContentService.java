package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentFindRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentCreateResponse create(String fileName, Long size, String contentType, byte[] bytes, String extension) {
//        byte[] attachment = request.attachment();
        BinaryContent binaryContent = binaryContentRepository.createBinaryContent(fileName, size, contentType, bytes, extension);
        return new BinaryContentCreateResponse(binaryContent.getId());
    }

    @Override
    public ResponseEntity<BinaryContent> find(UUID attachmentId) {
        BinaryContent binaryContent = Optional.ofNullable(binaryContentRepository.findById(attachmentId))
                .orElseThrow(() -> new IllegalArgumentException("no Binary Content matches"));// file | jcf : no exception

        return ResponseEntity.status(HttpStatus.OK)
                .body(binaryContent);
    }

    @Override
    public ResponseEntity<?> findAllByIdIn(BinaryContentFindRequest request) {
        if (request.attachments().isEmpty()) {
            throw new IllegalStateException("no ids in param");
        }
        List<UUID> attachmentIds = request.attachments();
//        한번에 리스트를 주고 완성된 값을 받는다.
//        무조건 받은 값이랑 일치하게 받을 때만 return or throw new exception

        // null 확인이 필요한가?: no 정확히 매핑 안되면 에러가 나오기 때문. attach는 무조건 값을 가지고 있다.
        List<BinaryContent> attachments = binaryContentRepository.findAllByIds(attachmentIds);

        List<String> attachmentPaths = new ArrayList<>();
        for (BinaryContent att : attachments) {
            attachmentPaths.add("/uploads/img/attachments/" + att.getId() + att.getExtension());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(attachmentPaths);
    }

    @Override
    public void delete(UUID attachmentId) {
        binaryContentRepository.deleteBinaryContentById(attachmentId); // file, jcf : throw exception
    }
}
