package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.NotFoundUserException;
import com.sprint.mission.discodeit.exception.UserNotInChannelException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    // 저장 로직과 userService, channelService의 기능 사용을 위한 생성자 주입
    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(Message message) {
        if (userService.findById(message.getSenderId()).isEmpty()) {
            throw new NotFoundUserException();
        }

        if (channelService.findById(message.getChannelId()).isEmpty()) {
            throw new NotFoundChannelException();
        }

        Channel foundChannel = channelService.findById(message.getChannelId()).get();

        // 메시지를 보낸 user의 mesagesList에 해당 메시지 추가
        userService.findById(message.getSenderId()).ifPresent(user -> {
            if (!user.getChannels().contains(foundChannel)) {
                throw new UserNotInChannelException();
            } else {
                user.getMessages().add(message);
                userService.update(user);
            }
        });

        // 메시지를 보낸 channel의 mesagesList에 해당 메시지 추가
        channelService.findById(message.getChannelId()).ifPresent(channel -> {
            channel.getMessageList().add(message);
            channelService.update(channel);
        });

        messageRepository.save(message);
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return messageRepository.findMessagesByChannelId(channelId);
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return messageRepository.findMessagesByUserId(userId);
    }

    @Override
    public List<Message> findMessageByContainingWord(String word) {
        return messageRepository.findMessageByContainingWord(word);
    }

    @Override
    public Message update(Message message) {
        // 변경된 메시지를 메시지를 보낸 User의 messageList에 반영
        userService.findAll().forEach(user -> {
            List<Message> messages = user.getMessages();
            for (int i=0; i<messages.size(); i++) {
                if (messages.get(i).equals(message)) {
                    messages.set(i, message);
                }
            }
            userService.update(user);
        });

        // 변경된 메시지를 메시지가 있는 Channel의 messageList에 반영
        channelService.findAll().forEach(channel -> {
            List<Message> messages = channel.getMessageList();
            for (int i=0; i<messages.size(); i++) {
                if (messages.get(i).equals(message)) {
                    messages.set(i, message);
                }
            }
            channelService.update(channel);
        });

        return messageRepository.save(message);
    }

    @Override
    public void deleteById(UUID messageId) {
        messageRepository.deleteById(messageId);

        // 메시지를 보낸 User의 messageList에서 해당 메시지 삭제
        userService.findAll().forEach(user -> {
            List<Message> messages = user.getMessages();
            for (int i=0; i<messages.size(); i++) {
                if (messages.get(i).getId().equals(messageId)) {
                    messages.remove(messages.get(i));
                }
            }
            userService.update(user);
        });

        // 메시지가 있는 Channel의 messageList에서 해당 메시지 삭제
        channelService.findAll().forEach(channel -> {
            List<Message> messages = channel.getMessageList();
            for (int i=0; i<messages.size(); i++) {
                if (messages.get(i).getId().equals(messageId)) {
                    messages.remove(messages.get(i));
                }
            }
            channelService.update(channel);
        });
    }
}
