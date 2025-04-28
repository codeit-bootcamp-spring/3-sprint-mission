//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//@Repository
//public class JCFChannelRepository implements ChannelRepository {
//    private final Map<UUID, Channel> channelData;
//
//    public JCFChannelRepository() {
//        this.channelData = new HashMap<>();
//    }
//
//    @Override
//    public Channel save(Channel channel) {
//        this.channelData.put(channel.getId(), channel);
//        return channel;
//    }
//
//    @Override
//    public Optional<Channel> findById(UUID id) {
//        return Optional.ofNullable(this.channelData.get(id));
//    }
//
//    @Override
//    public List<Channel> findAll() {
//        return this.channelData.values().stream().toList();
//    }
//
//    @Override
//    public List<Channel> findAllByUserId(UUID userId) {
//        return this.channelData.values().stream()
//                .filter(channel -> channel.getParicipantIds().equals(userId))
//                .toList();
//    }
//
//    @Override
//    public boolean existsById(UUID id) {
//        return this.channelData.containsKey(id);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        this.channelData.remove(id);
//    }
//}
