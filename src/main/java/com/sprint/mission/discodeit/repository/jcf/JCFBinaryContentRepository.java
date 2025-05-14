package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, List<BinaryContent>> binaryContents;

    public JCFBinaryContentRepository() {
        this.binaryContents = new HashMap<>();
    }

    @Override
    public BinaryContent createBinaryContent(BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        BinaryContent content = new BinaryContent(
                binaryContentCreateRequestDto.getOwnerId(),
                binaryContentCreateRequestDto.getContentType(),
                binaryContentCreateRequestDto.getData()
        );
        binaryContents
                .computeIfAbsent(binaryContentCreateRequestDto.getOwnerId(), k -> new ArrayList<>())
                .add(content);
        return content;
    }

    @Override
    public BinaryContent findBinaryContentById(UUID id) {
        List<BinaryContent> contents = binaryContents.get(id);
        if (contents == null || contents.isEmpty()) {
            return new BinaryContent(null, null, null);
        }
        return contents.stream().findFirst().orElse(null);
    }

    @Override
    public List<BinaryContent> findAllBinaryContentById(UUID id) {
        List<BinaryContent> contents = binaryContents.get(id);
        return contents == null ? List.of() : contents;
    }

    @Override
    public boolean deleteBinaryContentById(UUID id) {
        for (Map.Entry<UUID, List<BinaryContent>> entry : binaryContents.entrySet()) {
            List<BinaryContent> contents = entry.getValue();
            Iterator<BinaryContent> iterator = contents.iterator();

            while (iterator.hasNext()) {
                BinaryContent content = iterator.next();
                if (content.getId().equals(id)) {
                    iterator.remove();

                    if (contents.isEmpty()) {
                        binaryContents.remove(entry.getKey());
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
