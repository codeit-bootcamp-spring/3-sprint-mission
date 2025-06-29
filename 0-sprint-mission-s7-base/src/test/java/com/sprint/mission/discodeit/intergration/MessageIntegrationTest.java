package com.sprint.mission.discodeit.intergration;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import org.hamcrest.Matchers;
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
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("메시지 통합 테스트")
public class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    @Transactional
    @DisplayName("메시지 생성 - case : success")
    void createMessageSuccess() throws Exception {
        User user = userRepository.save(new User("김현기","test@test.com","009874",null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testChannel",null));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));

        MessageCreateRequest request = new MessageCreateRequest("testMessage",channel.getId(),user.getId());

        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile attachment = new MockMultipartFile(
            "attachment",
            "attachment.jpg",
            "image/jpeg",
            "attachment image".getBytes()
        );

        mockMvc.perform(multipart("/api/messages")
            .file(jsonPart)
            .file(attachment)
            .with(req -> {
                req.setMethod("POST");
                return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("testMessage"))
            .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
            .andExpect(jsonPath("$.author.id").value(user.getId().toString()));
    }

    @Test
    @DisplayName("메시지 생성 - case : 잘못된 요청으로 인한 failed")
    void createMessageFail() throws Exception {
        User user = userRepository.save(new User("김현기","test@test.com","009874",null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testChannel",null));
        MessageCreateRequest request = new MessageCreateRequest("",channel.getId(),user.getId());

        MockMultipartFile message = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages")
            .file(message))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("메시지 조회 - case : success")
    void readMessageSuccess() throws Exception {
        User user = userRepository.save(new User("김현기","test@test.com","009874",null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testChannel",null));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));

        Message message1 = messageRepository.save(new Message("test",channel,user,null));
        Message message2 = messageRepository.save(new Message("test2",channel,user,null));

        mockMvc.perform(get("/api/messages")
            .param("channelId",channel.getId().toString())
            .param("page","0")
            .param("size","10")
            .param("sort","createdAt,desc")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[*].content", Matchers.containsInAnyOrder("test", "test2")));
    }

    @Test
    @Transactional
    @DisplayName("메시지 수정 - case : success")
    void updateMessageSuccess() throws Exception {
        User user = userRepository.save(new User("김현기","test@test.com","009874",null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testChannel",null));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));

        Message message = messageRepository.save(new Message("test",channel,user,null));
        MessageUpdateRequest request = new MessageUpdateRequest("Hello Test");

        mockMvc.perform(patch("/api/messages/{messageId}",message.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Hello Test"));
    }

    @Test
    @Transactional
    @DisplayName("메시지 삭제 - case : success")
    void deleteMessageSuccess() throws Exception {
        User user = userRepository.save(new User("김현기","test@test.com","009874",null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testChannel",null));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));

        Message message = messageRepository.save(new Message("test",channel,user,null));

        mockMvc.perform(delete("/api/messages/{messageId}",message.getId()))
            .andExpect(status().isNoContent());
    }
}
