package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository extends AbstractFileRepository<UUID, List<BinaryContent>> implements BinaryContentRepository {

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/binaryContent.ser");
    }

    @Override
    public BinaryContent createBinaryContent(BinaryContent binaryContent) {
        Map<UUID, List<BinaryContent>> contents = loadFromFile();

        List<BinaryContent> ownerContents = contents.computeIfAbsent(
                binaryContent.getOwnerId(),
                k -> new ArrayList<>()
        );

        // 프로필 이미지인 경우 기존 프로필 이미지 제거
        if (binaryContent.getContentType() == BinaryContentType.PROFILE_IMAGE) {
            ownerContents.removeIf(content ->
                    content.getContentType() == BinaryContentType.PROFILE_IMAGE);
        }

        ownerContents.add(binaryContent);
        saveToFile(contents);
        return binaryContent;
    }


    @Override
    public BinaryContent findProfileImageByOwnerId(UUID ownerId) {
        Map<UUID, List<BinaryContent>> contents = loadFromFile();
        List<BinaryContent> ownerContents = contents.get(ownerId);

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
        Map<UUID, List<BinaryContent>> contents = loadFromFile();
        List<BinaryContent> ownerContents = contents.get(ownerId);

        if (ownerContents == null) {
            return new ArrayList<>();
        }

        return ownerContents.stream()
                .filter(content -> content.getContentType() == BinaryContentType.MESSAGE_ATTACHMENT)
                .collect(Collectors.toList());
    }


    @Override
    public boolean deleteBinaryContentById(UUID id) {
        Map<UUID, List<BinaryContent>> contents = loadFromFile();
        boolean deleted = false;

        for (List<BinaryContent> ownerContents : contents.values()) {
            if (ownerContents.removeIf(content -> content.getId().equals(id))) {
                deleted = true;
                break;
            }
        }

        if (deleted) {
            // 빈 리스트 제거
            contents.entrySet().removeIf(entry -> entry.getValue().isEmpty());
            saveToFile(contents);
        }

        return deleted;
    }

}
