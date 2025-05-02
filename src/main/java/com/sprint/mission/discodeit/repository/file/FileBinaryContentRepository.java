package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository extends AbstractFileRepository<UUID, List<BinaryContent>> implements BinaryContentRepository {

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/binaryContent.ser");
    }

    @Override
    public BinaryContent createBinaryContent(BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        Map<UUID, List<BinaryContent>> binaryContentFiles = loadFromFile();

        BinaryContent content = new BinaryContent(
                binaryContentCreateRequestDto.getOwnerId(),
                binaryContentCreateRequestDto.getContentType(),
                binaryContentCreateRequestDto.getData()
        );
        binaryContentFiles
                .computeIfAbsent(binaryContentCreateRequestDto.getOwnerId(), k -> new ArrayList<>())
                .add(content);
        saveToFile(binaryContentFiles);
        return content;
    }

    @Override
    public BinaryContent findBinaryContentById(UUID id) {
        Map<UUID, List<BinaryContent>> binaryContentFiles = loadFromFile();
        List<BinaryContent> contents = binaryContentFiles.get(id);
        if (contents == null || contents.isEmpty()) {
            return new BinaryContent(null, null, null);
        }
        return contents.stream().findFirst().orElse(null);
    }

    @Override
    public List<BinaryContent> findAllBinaryContentById(UUID id) {
        Map<UUID, List<BinaryContent>> binaryContentFiles = loadFromFile();
        List<BinaryContent> contents = binaryContentFiles.get(id);
        return contents == null ? List.of() : contents;
    }

    @Override
    public boolean deleteBinaryContentById(UUID id) {
        Map<UUID, List<BinaryContent>> binaryContentFiles = loadFromFile();
        for (Map.Entry<UUID, List<BinaryContent>> entry : binaryContentFiles.entrySet()) {
            List<BinaryContent> contents = entry.getValue();
            Iterator<BinaryContent> iterator = contents.iterator();

            while (iterator.hasNext()) {
                BinaryContent content = iterator.next();
                if (content.getId().equals(id)) {
                    iterator.remove();

                    if (contents.isEmpty()) {
                        binaryContentFiles.remove(entry.getKey());
                    }
                    saveToFile(binaryContentFiles);
                    return true;
                }
            }
        }
        return false;
    }
}
