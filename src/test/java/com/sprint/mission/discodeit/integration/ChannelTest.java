package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.response.JpaChannelResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.unit.basic.BasicChannelService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PackageName  : com.sprint.mission.discodeit.integration
 * FileName     : ChannelTest
 * Author       : dounguk
 * Date         : 2025. 6. 23.
 */
@DisplayName("Channel 통합 테스트")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChannelTest {

    @PersistenceContext
    private EntityManager em;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private JpaMessageRepository messageRepository;

    @Autowired
    private JpaChannelRepository channelRepository;

    @Autowired
    private JpaReadStatusRepository readStatusRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private BasicChannelService channelService;

    @Test
    @DisplayName("퍼블릭 채널 생성 프로세스가 정상적으로 동작한다.")
    void createPublic_success() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("channel", "description");

        // when
        JpaChannelResponse response = channelService.createChannel(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("channel");
        assertThat(response.getDescription()).isEqualTo("description");
        assertThat(response.getType()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("퍼블릭 채널 생성시 이름이 없을경우 MethodArgumentNotValidException을 반환한다.")
    void createPublic_noChannel_MethodArgumentNoValidException() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("   ", "설명");
        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("private 채널 생성 프로세스가 정상적으로 동작한다.")
    void createPrivate_success() throws Exception {
        // given
        User user = new User("paul","paul@test.com","1234");
        userRepository.save(user);

        User user2 = new User("daniel","daniel@test.com","1234");
        userRepository.save(user2);

        Set<UUID> participantIds = Set.of(user.getId(),user2.getId());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        // when
        JpaChannelResponse response = channelService.createChannel(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(ChannelType.PRIVATE);
        assertThat(response.getName()).isEqualTo("");
        assertThat(response.getDescription()).isEqualTo("");
        assertThat(response.getParticipants().size()).isEqualTo(2);
        assertThat(userRepository.findAll().size()).isEqualTo(2);
        assertThat(readStatusRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("유저가 충분히 없으면 UserNotFoundException을 반환한다.")
    void createPrivate_notEnoughUsers_UserNotFoundException() throws Exception {
        // given
        Set<UUID> participantIds = Set.of(UUID.randomUUID(),UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        // when
        UserNotFoundException result = assertThrows(UserNotFoundException.class, () -> channelService.createChannel(request));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("유저를 찾을 수 없습니다.");
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        assertThat(result.getDetails().get("users")).isEqualTo("not enough users in private channel");
    }

    @Test
    @DisplayName("퍼블릭 채널은 이름, 설명을 수정하는 프로세스가 정상 동작한다.")
    void updatePublic_success() throws Exception {
        // given
        Channel channel = Channel.builder()
            .type(ChannelType.PUBLIC)
            .name("channel")
            .build();
        channelRepository.save(channel);

        ChannelUpdateRequest request = new ChannelUpdateRequest("update channel", "update description");

        // when
        JpaChannelResponse result = channelService.update(channel.getId(), request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.getName()).isEqualTo("update channel");
        assertThat(result.getDescription()).isEqualTo("update description");
    }

    @Test
    @DisplayName("프라이빗 채널은 이름, 설명을 수정하려 할 수 없고 PrivateChannelUpdateException을 반환한다.")
    void updatePrivate_invalid_PrivateChannelUpdateException() throws Exception {
        // given
        Channel channel = Channel.builder()
            .type(ChannelType.PRIVATE)
            .build();
        channelRepository.save(channel);

        ChannelUpdateRequest request = new ChannelUpdateRequest("update channel", "update description");

        // when
        PrivateChannelUpdateException result = assertThrows(PrivateChannelUpdateException.class, () -> channelService.update(channel.getId(), request));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("프라이빗 채널은 수정이 불가능합니다.");
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.PRIVATE_CHANNEL_UPDATE);
        assertThat(result.getDetails().get("channelId")).isEqualTo(channel.getId());
    }

    @Test
    @DisplayName("채널 삭제 프로세스가 정상적으로 동작한다.")
    void deleteChannel_success() throws Exception {
        // given
        User user = new User("paul","paul@test.com","1234");
        userRepository.saveAndFlush(user);

        Channel channel = new Channel("channel","description");
        channelRepository.save(channel);

        for(int i = 0; i < 10; i++) {
            Message message = new Message(user, channel, "chat");
            messageRepository.save(message);
        }

        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatusRepository.save(readStatus);

        em.flush();
        em.clear();

        long beforeNumberOfMessage = messageRepository.count();
        long beforeNumberOfReadStatuses = readStatusRepository.count();

        // when
        channelService.deleteChannel(channel.getId());

        // then
        // channel + readStatus + message
        assertThat(channelRepository.findAll().size()).isEqualTo(0);
        assertThat(readStatusRepository.count()).isLessThan(beforeNumberOfReadStatuses);
        assertThat(messageRepository.count()).isLessThan(beforeNumberOfMessage);
        assertThat(messageRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("삭제 채널이 없을경우 ChannelNotFoundException을 반환한다.")
    void deleteChannel_noChannel_ChannelNotFoundException() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        // when
        ChannelNotFoundException exception = assertThrows(ChannelNotFoundException.class, () -> channelService.deleteChannel(channelId));

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("채널을 찾을 수 없습니다.");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND);
        assertThat(exception.getDetails().get("channelId")).isEqualTo(channelId);
    }
}
