package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.MessageService;

class BasicMessageServiceTest {

    private MessageService messageService;
    private JCFUserRepository userRepository;
    private JCFChannelRepository channelRepository;
    private JCFMessageRepository messageRepository;
    private User testAuthor;
    private Channel testChannel;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        JCFUserRepository.clearInstance();
        JCFChannelRepository.clearInstance();
        JCFMessageRepository.clearInstance();
        userRepository = JCFUserRepository.getInstance();
        channelRepository = JCFChannelRepository.getInstance();
        messageRepository = JCFMessageRepository.getInstance();
        userRepository.clearData();
        channelRepository.clearData();
        messageRepository.clearData();
        messageService = new BasicMessageService(userRepository, channelRepository, messageRepository);

        testAuthor = userRepository.save(new User("testAuthor", "author@test.com", "password"));
        testChannel = channelRepository.save(new Channel("TestChannel", false, "", testAuthor.getUserId()));
        testMessage = messageService.createMessage("Test message", testAuthor.getUserId(), testChannel.getChannelId());
    }

    @Test
    @DisplayName("메시지 생성 - 정상 케이스")
    void createMessage_Success() {
        Message createdMessage = messageService.createMessage("New message", testAuthor.getUserId(), testChannel.getChannelId());
        assertAll(
                () -> assertNotNull(createdMessage),
                () -> assertEquals("New message", createdMessage.getContent()),
                () -> assertEquals(testAuthor.getUserId(), createdMessage.getAuthorId()),
                () -> assertEquals(testChannel.getChannelId(), createdMessage.getChannelId())
        );
    }

    @Test
    @DisplayName("ID로 메시지 조회 - 존재하는 메시지")
    void getMessageById_ExistingMessage() {
        Message foundMessage = messageService.getMessageById(testMessage.getMessageId());
        assertAll(
                () -> assertNotNull(foundMessage),
                () -> assertEquals(testMessage.getContent(), foundMessage.getContent()),
                () -> assertEquals(testMessage.getAuthorId(), foundMessage.getAuthorId())
        );
    }

    @Test
    @DisplayName("채널별 메시지 조회")
    void getMessagesByChannel() {
        messageService.createMessage("Second message", testAuthor.getUserId(), testChannel.getChannelId());
        List<Message> channelMessages = messageService.getMessagesByChannel(testChannel.getChannelId());
        assertEquals(2, channelMessages.size());
    }

    @Test
    @DisplayName("메시지 삭제")
    void deleteMessage_Success() {
        UUID messageId = testMessage.getMessageId();
        messageService.deleteMessage(messageId);
        assertNull(messageService.getMessageById(messageId));
    }
}
