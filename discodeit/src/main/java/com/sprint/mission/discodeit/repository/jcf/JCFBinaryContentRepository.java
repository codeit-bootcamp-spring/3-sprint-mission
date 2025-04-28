//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.BinaryContent;
//import com.sprint.mission.discodeit.repository.BinaryContentRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//@Repository
//public class JCFBinaryContentRepository implements BinaryContentRepository {
//    private final Map<UUID, BinaryContent> binaryContentData;
//
//    public JCFBinaryContentRepository() {
//        this.binaryContentData = new HashMap<>();
//    }
//
//    @Override
//    public BinaryContent save(BinaryContent binaryContent) {
//        this.binaryContentData.put(binaryContent.getId(), binaryContent);
//        return binaryContent;
//    }
//
//    @Override
//    public Optional<BinaryContent> findById(UUID id) {
//        return Optional.ofNullable(this.binaryContentData.get(id));
//    }
//
//    @Override
//    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
//        return this.binaryContentData.values()
//                .stream()
//                .filter(binaryContent -> binaryContentIds.contains(binaryContent.getId()))
//                .toList();
//    }
//
//    @Override
//    public boolean existsById(UUID id) {
//        return this.binaryContentData.containsKey(id);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        this.binaryContentData.remove(id);
//    }
//}
