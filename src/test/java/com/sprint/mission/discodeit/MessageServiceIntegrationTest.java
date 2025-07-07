package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("메세지 서비스 통합 테스트")
@Transactional
public class MessageServiceIntegrationTest {

    @Autowired private MessageService messageService;
    @Autowired private BinaryContentRepository binaryContentRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MessageRepository messageRepository;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private BinaryContentStorage binaryContentStorage;

    private User setupUser() {
        BinaryContent binaryContent = new BinaryContent("test", 100L, "png");
        binaryContentRepository.save(binaryContent);
        binaryContentRepository.flush();

        binaryContentStorage.put(binaryContent.getId(), "test File".getBytes());

        BinaryContent findBinaryContent = binaryContentRepository.findById(binaryContent.getId()).orElseThrow();
        User user = new User("tester", "tester@example.com", "password1234", findBinaryContent);
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));
        user.setUserStatus(userStatus);
        userRepository.save(user);
        return user;
    }

    private Channel setupChannel() {
        Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");
        return channelRepository.save(channel);
    }

    @Test
    @DisplayName("메시지를 생성할 수 있어야 한다")
    void createMessage() {
        // given
        User user = setupUser();
        Channel channel = setupChannel();
        MessageCreateRequest request = new MessageCreateRequest("hello world", channel.getId(), user.getId());

        // when
        MessageDto messageDto = messageService.create(request, Collections.emptyList());

        // then
        Message saved = messageRepository.findById(messageDto.id()).orElseThrow();
        assertThat(saved.getContent()).isEqualTo("hello world");
        assertThat(saved.getChannel().getId()).isEqualTo(channel.getId());
        assertThat(saved.getAuthor().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("메시지를 수정할 수 있어야 한다")
    void updateMessage() {
        // given
        User user = setupUser();
        Channel channel = setupChannel();
        Message saved = messageRepository.save(new Message("old content", channel, user));
        MessageUpdateRequest update = new MessageUpdateRequest("new content");

        // when
        MessageDto updated = messageService.update(saved.getId(), update);

        // then
        assertThat(updated.content()).isEqualTo("new content");
    }

    @Test
    @DisplayName("메시지를 삭제할 수 있어야 한다")
    void deleteMessage() {
        // given
        User user = setupUser();
        Channel channel = setupChannel();
        Message message = messageRepository.save(new Message("will be deleted", channel, user));

        // when
        messageService.delete(message.getId());

        // then
        assertThat(messageRepository.findById(message.getId())).isEmpty();
    }

    @Test
    @DisplayName("채널의 메시지 목록을 조회할 수 있어야 한다")
    void findAllMessagesByChannel() {
        // given
        User user = setupUser();
        Channel channel = setupChannel();

        for (int i = 0; i < 5; i++) {
            messageRepository.save(new Message("msg" + i, channel, user));
        }

        Pageable pageable = PageRequest.of(0, 10);
        PageResponse<?> response = messageService.findAllByChannelId(channel.getId(), null, pageable);

        // then
        assertThat(response.content().size()).isEqualTo(5);
    }
}
