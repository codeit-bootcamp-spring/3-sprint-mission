package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Duration;
import java.time.Instant;
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
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("Message API 통합 테스트")
public class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private BinaryContentRepository binaryContentRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;

    private Message savedMessage;
    private Channel savedChannel;
    private User savedUser;
    private UserStatus savedUserStatus;
    private BinaryContent savedAttachment;

    @BeforeEach
    void setUp() {
        savedUser = new User("테스트유저", "test@codeit.com", "test1234", null, null);
        userRepository.save(savedUser);

        savedUserStatus = new UserStatus(savedUser,
                Instant.now().minus(Duration.ofMinutes(6)));  //offline 상태
        userStatusRepository.save(savedUserStatus);

        savedChannel = new Channel(ChannelType.PUBLIC, "공개채널테스트", "공개 채널 테스트입니다.");
        channelRepository.save(savedChannel);

        savedAttachment = new BinaryContent("test.png", 1024L, "image/png");
        binaryContentRepository.save(savedAttachment);

        savedMessage = new Message("테스트 메시지입니다.", savedChannel, savedUser,
                List.of(savedAttachment));
        messageRepository.save(savedMessage);
    }

    @Test
    @DisplayName("메시지 생성 요청 시 201 응답과 함께 생성된 메시지 정보를 반환한다.")
    void createMessage_Success() throws Exception {

        // given
        String content = "메시지 생성 테스트입니다.";
        UUID channelId = savedChannel.getId();
        UUID userId = savedUser.getId();

        MessageCreateRequest createRequest = new MessageCreateRequest(content, userId, channelId);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(createRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.id").value(userId.toString()));
    }

    @Test
    @DisplayName("메시지 수정 요청 시 200 응답과 함께 수정된 메시지 정보를 반환한다.")
    void updateMessage_Success() throws Exception {

        // given
        String newContent = "메시지 수정 테스트입니다.";
        UUID messageId = savedMessage.getId();

        MessageUpdateRequest updateRequest = new MessageUpdateRequest(newContent);

        // when & then
        mockMvc.perform(patch("/api/messages/" + messageId.toString())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value(newContent));
    }

    @Test
    @DisplayName("메시지 삭제 요청 시 204 응답과 함께 메시지가 삭제된다.")
    void deleteMessage_Success() throws Exception {

        // given
        UUID messageId = savedMessage.getId();

        // when & then
        mockMvc.perform(delete(("/api/messages/" + messageId.toString())))
                .andExpect(status().isNoContent());
        assertThat(messageRepository.findById(messageId).isPresent()).isFalse();
    }

//    @Test
//    @DisplayName("특정 채널의 메시지 전체 조회 요청시 200 응답과 함께 조회된 메시지 목록을 반환한다.")
//    void findAllByChannelId_Success() throws Exception {
//
//        // given
//        UUID channelId = savedChannel.getId();
//
//        // when & then
//        mockMvc.perform(get("/api/messages").param("channelId", channelId.toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].content").value(savedMessage.getContent()))
//                .andExpect(jsonPath("$.hasNext").value(false))
//                .andExpect(jsonPath("$.nextCursor").value(savedMessage.getCreatedAt().toString()))
//                .andExpect(jsonPath("$.size").value(20));
//    }
}
