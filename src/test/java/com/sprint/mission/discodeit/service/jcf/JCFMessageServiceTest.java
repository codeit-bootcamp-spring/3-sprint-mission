package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

class JCFMessageServiceTest {

    private JCFMessageService messageService;
    private UserService userService;
    private ChannelService channelService;
    private User testAuthor;
    private Channel testChannel;
    private Message testMessage;

    @BeforeEach
    public void setUp() {
        this.userService = new JCFUserService();
        this.channelService = new JCFChannelService(this.userService);
        this.messageService = new JCFMessageService(this.userService, this.channelService);

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
        assertAll(
                () -> assertNotNull(createdMessage),
                () -> assertEquals(content, createdMessage.getContent()),
                () -> assertEquals(testAuthor.getUserId(), createdMessage.getAuthorId()),
                () -> assertEquals(testChannel.getChannelId(), createdMessage.getChannelId())
        );
    }

    @Test
    @DisplayName("ID로 메시지 조회 - 존재하는 메시지")
    void getMessageById_ExistingMessage() {
        // Given - testMessage는 setUp()에서 생성됨
        UUID messageId = testMessage.getMessageId();

        // When
        Message foundMessage = messageService.getMessageById(messageId);

        // Then
        assertAll(
                () -> assertNotNull(foundMessage),
                () -> assertEquals(testMessage.getContent(), foundMessage.getContent()),
                () -> assertEquals(testMessage.getAuthorId(), foundMessage.getAuthorId())
        );
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
        assertAll(
                () -> assertNotNull(channelMessages),
                () -> assertEquals(3, channelMessages.size()) // testMessage + 2개 추가
        );
    }

    @Test
    @DisplayName("작성자별 메시지 조회")
    void getMessagesByAuthor() {
        // Given
        messageService.createMessage("Second message", testAuthor.getUserId(), testChannel.getChannelId());

        // When
        List<Message> authorMessages = messageService.getMessagesByAuthor(testAuthor.getUserId());

        // Then
        assertAll(
                () -> assertNotNull(authorMessages),
                () -> assertEquals(2, authorMessages.size()), // testMessage + 1개 추가
                () -> assertTrue(authorMessages.stream().allMatch(m -> m.getAuthorId().equals(testAuthor.getUserId())))
        );

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
        assertAll(
                () -> assertNotNull(updatedMessage),
                () -> assertEquals(newContent, updatedMessage.getContent())
        );
    }

    @Test
    @DisplayName("메시지 삭제")
    void deleteMessage_Success() {
        // Given
        UUID messageId = testMessage.getMessageId();

        // When
        messageService.deleteMessage(messageId);

        // Then
        assertAll(
                () -> assertNull(messageService.getMessageById(messageId)),
                () -> assertTrue(messageService.getMessagesByChannel(testChannel.getChannelId()).isEmpty())
        );
    }

    @Test
    @DisplayName("메시지 생성 시 존재하지 않는 작성자 ID 사용 시 예외 발생")
    void createMessage_shouldThrowExceptionForNonExistingAuthor() {
        // Given
        UUID nonExistingAuthorId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.createMessage("Fail message", nonExistingAuthorId, testChannel.getChannelId());
        });
    }

    @Test
    @DisplayName("메시지 생성 시 존재하지 않는 채널 ID 사용 시 예외 발생")
    void createMessage_shouldThrowExceptionForNonExistingChannel() {
        // Given
        UUID nonExistingChannelId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.createMessage("Fail message", testAuthor.getUserId(), nonExistingChannelId);
        });
    }

    @Test
    @DisplayName("메시지 생성 시 채널에 참가하지 않은 사용자가 작성 시 예외 발생")
    void createMessage_shouldThrowExceptionWhenAuthorIsNotParticipant() {
        // Given
        User nonParticipant = userService.createUser("nonParticipant", "non@test.com", "pass");
        // testChannel에는 testAuthor만 참가되어 있음

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            messageService.createMessage("Intruder message", nonParticipant.getUserId(), testChannel.getChannelId());
        });
    }

    @Test
    @DisplayName("존재하지 않는 메시지 업데이트 시도 시 예외 발생")
    void updateMessage_shouldThrowExceptionForNonExistingMessage() {
        // Given
        UUID nonExistingMessageId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.updateMessage(nonExistingMessageId, "Updated fail");
        });
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 시도 시 예외 발생")
    void deleteMessage_shouldThrowExceptionForNonExistingMessage() {
        // Given
        UUID nonExistingMessageId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.deleteMessage(nonExistingMessageId);
        });
    }
}
