package com.sprint.mission.discodeit.repository;

import static com.sprint.mission.discodeit.fixture.MessageFixture.createMessage;
import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MessageRepository 단위 테스트")
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private TestEntityManager em;

    private User user;
    private Channel channel;
    private Message message;

    @BeforeEach
    void setUp() {
        user = new User("테스트유저", "test@codeit.com", "test1234", null, null);
        channel = new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
        message = createMessage("안녕하세요", channel, user, Instant.now());

        ReflectionTestUtils.setField(user, "createdAt", Instant.now());
        ReflectionTestUtils.setField(channel, "createdAt", Instant.now());

        userRepository.save(user);
        channelRepository.save(channel);
        messageRepository.save(message);
    }

    @BeforeAll
    static void beforeAll() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }


    @Test
    @DisplayName("채널의 가장 최근에 생성된 메시지를 조회한다.")
    void shouldReturnLatestMessage_whenGivenChannelId() {

        // given
        Message expected = createMessage("가장 최근 메시지 테스트", channel, user,
                Instant.now().plusSeconds(10));
        messageRepository.save(expected);

        // when
        Optional<Message> lastMessage = messageRepository.findTopByChannel_IdOrderByCreatedAtDesc(
                channel.getId());

        // then
        assertThat(lastMessage.isPresent()).isTrue();
        assertThat(lastMessage.get().getId()).isEqualTo(expected.getId());
        assertThat(lastMessage.get().getCreatedAt()).isAfter(message.getCreatedAt());
    }

    @Test
    @DisplayName("채널의 모든 메시지를 삭제한다.")
    void shouldDeleteAllMessages_whenGivenChannelId() {

        // given
        Channel nontargetChannel = new Channel(ChannelType.PRIVATE, null, null);
        ReflectionTestUtils.setField(nontargetChannel, "createdAt", Instant.now());
        channelRepository.save(nontargetChannel);

        Message message1 = createMessage("메시지1", channel, user, Instant.now());
        Message message2 = createMessage("메시지2", channel, user, Instant.now());
        Message message3 = createMessage("메시지3", nontargetChannel, user, Instant.now());
        messageRepository.saveAll(List.of(message1, message2, message3));

        // when
        messageRepository.deleteAllByChannelId(channel.getId());

        // then
        List<Message> remainingMessages = messageRepository.findAll();
        assertThat(remainingMessages).hasSize(1);
        assertThat(remainingMessages.get(0)).isEqualTo(message3);
    }

    @Test
    @DisplayName("채널의 기준 시간 이전 메시지들을 조회한다.")
    void shouldReturnMessagesBeforeCursorTime_whenGivenChannelId() throws InterruptedException {

        // given
        messageRepository.deleteAll();

        UserStatus userStatus = new UserStatus(user, Instant.now());
        ReflectionTestUtils.setField(userStatus, "createdAt", Instant.now());
        userStatusRepository.save(userStatus);
        user.setStatus(userStatus);
        userRepository.save(user);

        Message oldMessage = createMessage("이전 메시지", channel, user, Instant.now());
        messageRepository.save(oldMessage);

        Thread.sleep(3000);

        // 메세지 2 - 이후 시간
        Message recentMessage = createMessage("이후 메시지", channel, user, Instant.now());
        messageRepository.save(recentMessage);

        // flush & clear로 반영 강제
        em.flush();
        em.clear();

        Instant cursorTime = oldMessage.getCreatedAt().plusMillis(1500);

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channel.getId(),
                cursorTime, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        Message resultMessage = result.getContent().get(0);
        assertThat(resultMessage.getContent()).isEqualTo("이전 메시지");

        // 유저 확인
        User author = result.getContent().get(0).getAuthor();
        assertThat(author.getUsername()).isEqualTo("테스트유저");
        assertThat(author.getEmail()).isEqualTo("test@codeit.com");
    }

    @Test
    @DisplayName("기준 시간보다 이전에 작성된 메시지가 없는 경우 빈 Slice를 반환한다.")
    void shouldReturnEmptySlice_whenNoMessagesBeforeCursorTime() {

        // given
        Instant cursorTime = Instant.parse("2025-01-01T01:00:00Z");
        Instant afterCursorTime = cursorTime.plusSeconds(60);

        Message recentMessage = createMessage("기준 이후 메시지", channel, user, afterCursorTime);
        messageRepository.save(recentMessage);

        em.flush();
        em.clear();

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channel.getId(),
                cursorTime, PageRequest.of(0, 10));

        // then
        assertThat(result).isEmpty();
    }
}
