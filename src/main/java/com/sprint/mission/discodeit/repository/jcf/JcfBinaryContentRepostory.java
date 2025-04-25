package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.jcf
 * fileName       : JcfBinaryContentRepostory
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */
@Primary
@Repository
public class JcfBinaryContentRepostory implements BinaryContentRepository {
    Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public BinaryContent createBinaryContent(byte[] image) {
        BinaryContent binaryContent = new BinaryContent(image);
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    // 완성 필요
//    @Override
//    public BinaryContent findBinaryContentByUserId(UUID userId) {
//
//        return null;
//    }

    // 완성 필요

    @Override
    public BinaryContent updateImage(UUID profileId, byte[] image) {
        BinaryContent binaryContent = data.get(profileId);
        binaryContent.setImage(image);
        return binaryContent;
    }
}
