package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private static volatile JCFBinaryContentRepository instance;
    private final Map<UUID, BinaryContent> binaryContents = new ConcurrentHashMap<>();

    private JCFBinaryContentRepository() {}

    public static JCFBinaryContentRepository getInstance() {
        JCFBinaryContentRepository result = instance;
        if (result == null) {
            synchronized (JCFBinaryContentRepository.class) {
                result = instance;
                if (result == null) {
                    instance = result = new JCFBinaryContentRepository();
                }
            }
        }
        return result;
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        binaryContents.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(binaryContents.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(binaryContents::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return binaryContents.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        binaryContents.remove(id);
    }
}
