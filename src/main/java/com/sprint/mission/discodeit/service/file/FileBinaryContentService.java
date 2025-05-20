package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileBinaryContentService
 * author         : doungukkim
 * date           : 2025. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 29.        doungukkim       최초 생성
 */
@Service("fileBinaryService")
@RequiredArgsConstructor
public class FileBinaryContentService  {
//    private final BinaryContentRepository binaryContentRepository;
//
//    @Override
//    public BinaryContentCreateResponse create() {
//
//        BinaryContent binaryContent = binaryContentRepository.createBinaryContent();
//        return new BinaryContentCreateResponse(binaryContent.getId());
//    }
//
//    public BinaryContent find(UUID attachmentId) {
//        Objects.requireNonNull(attachmentId, "no param");
//
//        return Optional.ofNullable(binaryContentRepository
//                        .findById(attachmentId))
//                .orElseThrow(() -> new IllegalArgumentException("no Binary Content matches"));
//    }
//
//    public List<BinaryContent> findAllByIdIn(List<UUID> attachmentIds) {
//        Objects.requireNonNull(attachmentIds, "no attachmentIds");
//        if (attachmentIds.isEmpty()) {
//            throw new RuntimeException("no ids in param");
//        }
//        return binaryContentRepository.findAllByIds(attachmentIds);
//    }
//
//    @Override
//    public void delete(UUID attachmentId) {
//        binaryContentRepository.deleteBinaryContentById(attachmentId);
//    }
}
