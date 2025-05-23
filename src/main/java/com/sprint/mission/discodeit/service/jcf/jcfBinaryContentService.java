package com.sprint.mission.discodeit.service.jcf;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : jcfBinaryContentService
 * author         : doungukkim
 * date           : 2025. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 29.        doungukkim       최초 생성
 */
@Service("jcfBinaryContentService")
@RequiredArgsConstructor
public class jcfBinaryContentService {
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
//
//    public List<BinaryContent> findAllByIdIn(List<UUID> attachmentIds) {
//        Objects.requireNonNull(attachmentIds, "no attachmentIds");
//        if (attachmentIds.isEmpty()) {
//            throw new RuntimeException("no ids in param");
//        }
//        return binaryContentRepository.findAllByIds(attachmentIds);
//    }



//    @Override
//    public void delete(UUID attachmentId) {
//        binaryContentRepository.deleteBinaryContentById(attachmentId);
//    }
}
