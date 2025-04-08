package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

class JCFMessageServiceTest {

    private JCFMessageService messageService;
    private JCFUserService userService;
    private JCFChannelService channelService;
    private User testAuthor;
    private Channel testChannel;
    private Message testMessage;

    @BeforeEach
    public void setUp() {
        this.messageService = new JCFMessageService();
        this.userService = new JCFUserService();
        this.channelService = new JCFChannelService();

        // 테스트용 사용자, 채널, 메시지 생성
        testAuthor = userService.createUser("testAuthor", "author@test.com", "password");
        testChannel = channelService.createChannel("TestChannel", false, "", testAuthor.getUserId());
        testMessage = messageService.createMessage("Test message", testAuthor.getUserId(), testChannel.getChannelId());
    }

    @Test
    @DisplayName("메시지 생성 - 정상 케이스")
    void createMessage_Success() {
        // Given
        String content = "New message";
        // When
        Message createdMessage = messageService.createMessage(content, testAuthor.getUserId(), testChannel.getChannelId());

        // Then
        assertNotNull(createdMessage);
        assertEquals(content, createdMessage.getContent());
        assertEquals(testAuthor.getUserId(), createdMessage.getAuthorId());
        assertEquals(testChannel.getChannelId(), createdMessage.getChannelId());
    }

    @Test
    @DisplayName("ID로 메시지 조회 - 존재하는 메시지")
    void getMessageById_ExistingMessage() {
        // Given - testMessage는 setUp()에서 생성됨
        UUID messageId = testMessage.getMessageId();

        // When
        Message foundMessage = messageService.getMessageById(messageId);

        // Then
        assertNotNull(foundMessage);
        assertEquals(testMessage.getContent(), foundMessage.getContent());
        assertEquals(testMessage.getAuthorId(), foundMessage.getAuthorId());
    }

    @Test
    @DisplayName("채널별 메시지 조회")
    void getMessagesByChannel() {
        // Given
        messageService.createMessage("Second message", testAuthor.getUserId(), testChannel.getChannelId());
        messageService.createMessage("Third message", testAuthor.getUserId(), testChannel.getChannelId());

        // When
        List<Message> channelMessages = messageService.getMessagesByChannel(testChannel.getChannelId());

        // Then
        assertNotNull(channelMessages);
        assertEquals(3, channelMessages.size()); // testMessage + 2개 추가
    }

    @Test
    @DisplayName("작성자별 메시지 조회")
    void getMessagesByAuthor() {
        // Given
        messageService.createMessage("Second message", testAuthor.getUserId(), testChannel.getChannelId());

        // When
        List<Message> authorMessages = messageService.getMessagesByAuthor(testAuthor.getUserId());

        // Then
        assertNotNull(authorMessages);
        assertEquals(2, authorMessages.size()); // testMessage + 1개 추가
        assertTrue(authorMessages.stream().allMatch(m -> m.getAuthorId().equals(testAuthor.getUserId())));
    }

    @Test
    @DisplayName("메시지 내용 업데이트")
    void updateMessage_Success() {
        // Given
        String newContent = "Updated content";

        // When
        messageService.updateMessage(testMessage.getMessageId(), newContent);
        Message updatedMessage = messageService.getMessageById(testMessage.getMessageId());

        // Then
        assertNotNull(updatedMessage);
        assertEquals(newContent, updatedMessage.getContent());
    }

    @Test
    @DisplayName("메시지 삭제")
    void deleteMessage_Success() {
        // Given
        UUID messageId = testMessage.getMessageId();

        // When
        messageService.deleteMessage(messageId);

        // Then
        assertNull(messageService.getMessageById(messageId));
        assertTrue(messageService.getMessagesByChannel(testChannel.getChannelId()).isEmpty());
    }
}
