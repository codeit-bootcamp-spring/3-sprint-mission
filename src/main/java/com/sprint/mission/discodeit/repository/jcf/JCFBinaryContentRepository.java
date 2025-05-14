package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, List<BinaryContent>> binaryContents;

    public JCFBinaryContentRepository() {
        this.binaryContents = new HashMap<>();
    }

    @Override
    public BinaryContent createBinaryContent(BinaryContent binaryContent) {
        List<BinaryContent> ownerContents = binaryContents.computeIfAbsent(
                binaryContent.getOwnerId(),
                k -> new ArrayList<>()
        );

        // 프로필 이미지인 경우 기존 프로필 이미지 제거
        if (binaryContent.getContentType() == BinaryContentType.PROFILE_IMAGE) {
            ownerContents.removeIf(content ->
                    content.getContentType() == BinaryContentType.PROFILE_IMAGE);
        }

        ownerContents.add(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent findProfileImageByOwnerId(UUID ownerId) {
        List<BinaryContent> ownerContents = binaryContents.get(ownerId);
        if (ownerContents == null) {
            return null;
        }

        return ownerContents.stream()
                .filter(content -> content.getContentType() == BinaryContentType.PROFILE_IMAGE)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<BinaryContent> findAllAttachmentsByOwnerId(UUID ownerId) {
        List<BinaryContent> ownerContents = binaryContents.get(ownerId);
        if (ownerContents == null) {
            return new ArrayList<>();
        }

        return ownerContents.stream()
                .filter(content -> content.getContentType() == BinaryContentType.MESSAGE_ATTACHMENT)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteBinaryContentById(UUID id) {
        boolean deleted = false;

        for (List<BinaryContent> ownerContents : binaryContents.values()) {
            if (ownerContents.removeIf(content -> content.getId().equals(id))) {
                deleted = true;
                break;
            }
        }

        if (deleted) {
            // 빈 리스트 제거
            binaryContents.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        }

        return deleted;
    }

}
