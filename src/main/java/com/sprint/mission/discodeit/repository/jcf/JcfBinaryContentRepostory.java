package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

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
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
@Repository
public class JcfBinaryContentRepostory implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data = new HashMap<>();


    @Override
    public BinaryContent findById(UUID attachmentId) {
        return data.get(attachmentId);
    }


    @Override
    public List<BinaryContent> findAllByIds(List<UUID> attachmentIds) {

        List<BinaryContent> selectedAttachments = new ArrayList<>();
        for (UUID attachmentId : attachmentIds) {
            if (data.containsKey(attachmentId)) {
                selectedAttachments.add(data.get(attachmentId));
            }
        }
        if (selectedAttachments.size() == attachmentIds.size()) {
            return selectedAttachments;
        }

        throw new RuntimeException("no attachment matches with id");
    }

    @Override
    public BinaryContent createBinaryContent(byte[] image) {
        BinaryContent binaryContent = new BinaryContent(image);
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent updateImage(UUID profileId, byte[] image) {
        if (data.get(profileId) == null) {
            throw new IllegalStateException("no image to update");
        }
        BinaryContent binaryContent = data.get(profileId);
        binaryContent.setAttachment(image);
        return binaryContent;
    }


    @Override
    public void deleteBinaryContentById(UUID attachmentId) {
        if (!data.containsKey(attachmentId)) {
            throw new IllegalStateException("no data to delete");
        }
        data.remove(attachmentId);
    }
}
