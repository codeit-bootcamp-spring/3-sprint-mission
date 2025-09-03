package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    private Channel channel;
    private User author;

    @BeforeEach
    void setup() {
        BinaryContent profile = new BinaryContent("image.png", 1024L, "image/png");
        author = new User("testuser", "test@email.com", "password", null, Role.USER);
        userRepository.save(author);

        channel = new Channel(ChannelType.PUBLIC, "test-channel", null);
        channelRepository.save(channel);
    }

    @Test
    @DisplayName("메시지 조회 실패 - 존재하지 않는 채널 ID")
    void shouldReturnEmpty_whenNoMessagesFoundByChannelId() {
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
            UUID.randomUUID(),
            Instant.now(),
            PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("메시지 조회 성공 - 채널 ID로 작성자 포함")
    void shouldReturnMessagesWithAuthor_whenChannelIdIsValid() {
        Instant now = Instant.now();
        Instant cursor = now.plusSeconds(60);

        Message m1 = new Message("first", channel, author, List.of());
        Message m2 = new Message("second", channel, author, List.of());

        ReflectionTestUtils.setField(m1, "createdAt", now.minusSeconds(30));
        ReflectionTestUtils.setField(m2, "createdAt", now.minusSeconds(10));
        ReflectionTestUtils.setField(m1, "updatedAt", now.minusSeconds(30));
        ReflectionTestUtils.setField(m2, "updatedAt", now.minusSeconds(10));

        messageRepository.saveAll(List.of(m1, m2));
        messageRepository.flush();

        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
            channel.getId(),
            cursor,
            PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("content")
            .containsExactlyInAnyOrder("first", "second");
    }

    @Test
    @DisplayName("마지막 메시지 시간 조회 성공")
    void shouldReturnLastMessageTime_whenChannelIdIsValid() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        Message m1 = new Message("1st", channel, author, List.of());
        Message m2 = new Message("2nd", channel, author, List.of());

        ReflectionTestUtils.setField(m1, "createdAt", now.minusSeconds(60));
        ReflectionTestUtils.setField(m2, "createdAt", now);

        messageRepository.save(m1);
        messageRepository.save(m2);

        Instant result = messageRepository.findLastMessageAtByChannelId(channel.getId())
            .orElse(Instant.MIN);

        assertThat(result).isCloseTo(now, within(50, ChronoUnit.MILLIS));
    }

    @Test
    @DisplayName("마지막 메시지 시간 조회 실패 - 존재하지 않는 채널 ID")
    void shouldReturnEmpty_whenNoMessagesExistForChannel() {
        Instant result = messageRepository.findLastMessageAtByChannelId(UUID.randomUUID())
            .orElse(Instant.MIN);

        assertThat(result).isEqualTo(Instant.MIN);
    }

    @Test
    @DisplayName("채널 ID로 메시지 삭제 성공")
    void shouldDeleteMessagesByChannelIdSuccessfully() {
        messageRepository.save(new Message("delete me", channel, author, List.of()));
        messageRepository.deleteAllByChannelId(channel.getId());

        List<Message> result = messageRepository.findAll();
        assertThat(result).isEmpty();
    }
}
