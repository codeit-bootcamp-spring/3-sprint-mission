package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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

import java.time.Instant;
import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("메세지 서비스 통합 테스트")
@Transactional
public class MessageServiceIntegrationTest {

    @Autowired private BinaryContentRepository binaryContentRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MessageRepository messageRepository;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private BinaryContentStorage binaryContentStorage;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private User setupUser() {
        BinaryContent binaryContent = new BinaryContent("test", 100L, "png");
        binaryContentRepository.saveAndFlush(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), "test File".getBytes());
        User user = new User("tester", "tester@example.com", "password1234", binaryContent);
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));
        user.setUserStatus(userStatus);
        return userRepository.save(user);
    }

    private Channel setupChannel() {
        return channelRepository.saveAndFlush(new Channel(ChannelType.PUBLIC, "test", "desc"));
    }

    @Test
    @DisplayName("메시지를 생성할 수 있어야 한다")
    void createMessage() throws Exception {
        // given
        User user = setupUser();
        Channel channel = setupChannel();

        String messageCreateRequestJson = String.format("""
        {
            "content": "hello world",
            "channelId": "%s",
            "authorId": "%s"
        }
    """, channel.getId(), user.getId());

        MockMultipartFile jsonPart = new MockMultipartFile(
                "messageCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                messageCreateRequestJson.getBytes()
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "attachments",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "file content".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(jsonPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("hello world"));

        Message saved = messageRepository.findAll().get(0);
        assertThat(saved.getContent()).isEqualTo("hello world");
        assertThat(saved.getChannel().getId()).isEqualTo(channel.getId());
        assertThat(saved.getAuthor().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("메시지를 수정할 수 있어야 한다")
    void updateMessage() throws Exception {
        User user = setupUser();
        Channel channel = setupChannel();
        Message message = messageRepository.save(new Message("old content", channel, user));
        MessageUpdateRequest update = new MessageUpdateRequest("new content");

        mockMvc.perform(patch("/api/messages/{id}", message.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("new content"));

        Message updated = messageRepository.findById(message.getId()).orElseThrow();
        assertThat(updated.getContent()).isEqualTo("new content");
    }

    @Test
    @DisplayName("메시지를 삭제할 수 있어야 한다")
    void deleteMessage() throws Exception {
        User user = setupUser();
        Channel channel = setupChannel();
        Message message = messageRepository.save(new Message("to delete", channel, user));

        mockMvc.perform(delete("/api/messages/{id}", message.getId()))
                .andExpect(status().isNoContent());

        assertThat(messageRepository.findById(message.getId())).isEmpty();
    }

    @Test
    @DisplayName("채널의 메시지 목록을 조회할 수 있어야 한다")
    void findAllMessagesByChannel() throws Exception {
        // given
        User user = setupUser();
        Channel channel = setupChannel();

        for (int i = 0; i < 5; i++) {
            messageRepository.save(new Message("msg" + i, channel, user));
        }

        // when & then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString())
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5));

        List<Message> messages = messageRepository.findAllByChannelId(channel.getId());
        assertThat(messages.size()).isEqualTo(5);
    }
}
