package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private BinaryContentRepository binaryContentRepository;

    private Channel channel;
    private User user;

    @BeforeEach
    void setUp() {
        channel = new Channel(ChannelType.PUBLIC, "testChannel", "testDescription");
        channelRepository.save(channel);

        BinaryContent profile = new BinaryContent("testImg.jpg", 10L, "image/jpeg");
        binaryContentRepository.save(profile);

        user = new User("test", "testemail@test.com", "pw", profile);
        userRepository.save(user);

        UserStatus status = new UserStatus(user, Instant.now());
        userStatusRepository.save(status);
    }

    @Test
    @DisplayName("채널 ID와 생성 시간 기준으로 이전 메시지를 페이징하여 조회할 수 있다")
    void findByChannelIdAndCreatedAtBefore_success() {
        // given
        Message message1 = new Message("hello", channel, user, Collections.emptyList());
        Message message2 = new Message("world", channel, user, Collections.emptyList());
        messageRepository.saveAll(List.of(message1, message2));

        // when
        Slice<Message> slice = messageRepository.findByChannelIdAndCreatedAtBefore(
            channel.getId(),
            Instant.now(),
            PageRequest.of(0, 10)
        );

        // then
        assertThat(slice).isNotNull();
        assertThat(slice.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("채널의 마지막 메시지 시간을 조회할 수 있다")
    void findLastMessageAt_success() {
        // given
        Message message = new Message("test", channel, user, Collections.emptyList());
        messageRepository.save(message);

        // when
        Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

        // then
        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("메시지가 없는 채널에서는 마지막 메시지 시간이 없다")
    void findLastMessageAt_empty() {
        // when
        Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널의 모든 메시지를 삭제할 수 있다")
    void deleteAllByChannelId_success() {
        // given
        Message message1 = new Message("hello", channel, user, Collections.emptyList());
        Message message2 = new Message("world", channel, user, Collections.emptyList());
        messageRepository.saveAll(List.of(message1, message2));

        // when
        messageRepository.deleteAllByChannelId(channel.getId());

        // then
        assertThat(messageRepository.findAll()).isEmpty();
    }
}