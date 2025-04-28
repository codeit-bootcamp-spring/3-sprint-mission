//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//@Repository
//public class JCFMessageRepository implements MessageRepository {
//    private final Map<UUID, Message> messageData;
//
//    public JCFMessageRepository() {
//        this.messageData = new HashMap<>();
//    }
//
//    @Override
//    public Message save(Message message) {
//        this.messageData.put(message.getId(), message);
//        return message;
//    }
//
//    @Override
//    public Optional<Message> findById(UUID id) {
//        return Optional.ofNullable(this.messageData.get(id));
//    }
//
//    @Override
//    public List<Message> findAllByChannelId(UUID channelId) {
//        return this.messageData.values()
//                .stream()
//                .filter(message -> message.getChannelId().equals(channelId))
//                .toList();
//    }
//
//    @Override
//    public boolean existsById(UUID id) {
//        return this.messageData.containsKey(id);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        this.messageData.remove(id);
//    }
//
//    @Override
//    public void deleteByChannelId(UUID channelId) {
//        this.findAllByChannelId(channelId)
//                .forEach(message -> this.deleteById(message.getId()));
//    }
//}
