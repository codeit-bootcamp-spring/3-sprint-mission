package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {

    private final CopyOnWriteArrayList<Message> data;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public JCFMessageRepository(CopyOnWriteArrayList<Message> data, UserRepository userRepository, ChannelRepository channelRepository) {
        this.data = data;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public void save(Message message,User user,Channel channel) {
        try {
            if (channel.getMembers().contains(user)) {
                data.add(message);
            } else {
                throw new NoSuchElementException();
            }
        }catch (NoSuchElementException e){
            System.out.println("해당 채널에 존재하지 않는 사용자입니다.");
        }
    }

    @Override
    public void read() {
        data.stream()
                .forEach(System.out::println);
    }

    @Override
    public void readById(UUID id) {
        data.stream()
                .filter((n)->n.getId().equals(id))
                .forEach(System.out::println);
    }
    @Override
    public void update(UUID id, Message message) {
        data.stream()
                .filter(mess->mess.getId().equals(id))
                .forEach(mess->{
                    mess.updateUpdatedAt(Instant.now());
                    mess.updateEmoticon(message.getEmoticon());
                    mess.updatePicture(message.getPicture());
                    mess.updateText(message.getText());});
    }

    @Override
    public void delete(Message message) {
        data.remove(message);
    }
    }
