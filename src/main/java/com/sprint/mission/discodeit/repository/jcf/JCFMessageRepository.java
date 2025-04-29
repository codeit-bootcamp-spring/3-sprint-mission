package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data; // messageId

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public JCFMessageRepository(ChannelRepository channelRepository, UserRepository userRepository) {
        this.data = new HashMap<UUID, Message>();
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }


    @Override
    public void create(Message message) { // content, channelId, authorId가 담긴 Message 타입을 받아옴
        try {
            channelRepository.findById(message.getChannelId()); // 비즈니스 로직
            userRepository.findById(message.getAuthorId()); // 비즈니스 로직
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.data.put(message.getId(), message); // 저장 로직

    }

    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);
//        if (message == null) { 비즈니스 로직
//            return null;
//        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return this.data.values().stream().collect(Collectors.toList());
    }

    @Override
    public void updateMessage(UUID id, String newContent) {
        Message message = this.findById(id);

        message.updateMessage(newContent);

    }

    @Override
    public void delete(UUID id) {

        Message message = this.findById(id);
        this.data.remove(id);
    }
}
