package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "JCF",matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final CopyOnWriteArrayList<BinaryContent> data = new CopyOnWriteArrayList<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.add(binaryContent);
        return binaryContent;
    }

    @Override
    public List<BinaryContent> read() {
        return data;
    }

    @Override
    public Optional<BinaryContent> readById(UUID id) {
        Optional<BinaryContent> binaryContent = data.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return binaryContent;
    }

    @Override
    public void update(UUID id, BinaryContent binaryContent) {
        data.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setContentType(binaryContent.getContentType());
                    c.setBytes(binaryContent.getBytes());
                });
    }

    @Override
    public void delete(UUID binaryContentId) {
        data.removeIf(binaryContent -> binaryContent.getId().equals(binaryContentId));
    }

}
