package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("Message API 통합테스트")
public class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    private User savedUser1;
    private User savedUser2;
    private Channel savedPublicChannel;
    private Channel savedPrivateChannel;
    private Message savedMessage;
    private BinaryContent savedBinaryContent;
    private BinaryContent savedFile;

    @BeforeEach
    void setUp() {
        // BinaryContent 생성
        BinaryContent binaryContent1 = new BinaryContent("profile1.png", 102400L, "image/png");
        savedBinaryContent = binaryContentRepository.save(binaryContent1);

        BinaryContent binaryContent2 = new BinaryContent("file1.pdf", 204800L, "application/pdf");
        savedFile = binaryContentRepository.save(binaryContent2);

        // User 생성
        User user1 = new User("조현아", "zzo@email.com", "password123!", savedBinaryContent);
        savedUser1 = userRepository.save(user1);

        User user2 = new User("투현아", "z2@email.com", "password123!", null);
        savedUser2 = userRepository.save(user2);

        // UserStatus 생성
        UserStatus userStatus1 = new UserStatus(savedUser1, Instant.now());
        userStatusRepository.save(userStatus1);

        UserStatus userStatus2 = new UserStatus(savedUser2, Instant.now());
        userStatusRepository.save(userStatus2);

        // Channel 생성
        Channel publicChannel = new Channel(ChannelType.PUBLIC, "채널1", "통합 테스트 공개채널");
        savedPublicChannel = channelRepository.save(publicChannel);

        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        savedPrivateChannel = channelRepository.save(privateChannel);

        // Message 생성
        Message message1 = new Message("메시지1", savedPublicChannel, savedUser1, null);
        savedMessage = messageRepository.save(message1);

        Message message2 = new Message("메시지2", savedPrivateChannel, savedUser2, List.of(savedFile));
        messageRepository.save(message2);

        // ReadStatus 생성
        ReadStatus readStatus = new ReadStatus(savedUser1, savedPublicChannel, Instant.now());
        readStatusRepository.save(readStatus);
    }

    @Test
    @DisplayName("메시지 생성 - 성공")
    void createMessage_Success() throws Exception {
        // Given
        UUID channelId = savedPublicChannel.getId();
        UUID authorId = savedUser1.getId();

        MessageCreateRequest request = new MessageCreateRequest("통합 테스트 내용", channelId, authorId);
        String json = objectMapper.writeValueAsString(request);

        MockMultipartFile messagePart = new MockMultipartFile(
            "messageCreateRequest", "", "application/json", json.getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .file(messagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("통합 테스트 내용"))
            .andDo(print());
    }

    @Test
    @DisplayName("메시지 조회 - 성공")
    void getMessagesByChannelId() throws Exception {
        // Given
        UUID channelId = savedPublicChannel.getId();

        // When & Then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("page", "0")     // 페이지 번호
                .param("size", "10"))   // 페이지 당 항목 수
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andDo(print());
    }

    @Test
    @DisplayName("메시지 수정 - 성공")
    void updateMessage() throws Exception {
        // Given
        UUID messageId = savedMessage.getId();

        MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
        String json = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("수정된 내용"))
            .andDo(print());
    }

    @Test
    @DisplayName("메시지 삭제 - 성공")
    void deleteMessage() throws Exception {
        // Given
        UUID messageId = savedMessage.getId();

        // When & Then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNoContent());
    }
}
