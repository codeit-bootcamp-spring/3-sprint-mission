package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Channel testChannel;
    private User testUser;
    private Instant now;
    private Instant t1;
    private Instant t2;
    private Instant t3;

    private Message createMessage(String content, Instant createdAt) {
        Message message = new Message(content, testChannel, testUser, null);
        ReflectionTestUtils.setField(message, "createdAt", createdAt);
        return messageRepository.save(message);
    }

    @BeforeEach
    void setUp() {
        now = Instant.now();
        t1 = now.minus(10, ChronoUnit.MINUTES);
        t2 = now.minus(5, ChronoUnit.MINUTES);
        t3 = now;

        testUser = userRepository.save(new User("testuser", "test@abc.com", "1234", null));
        testChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "테스트 채널", "설명"));

        createMessage("첫 번째 메시지", t1);
        createMessage("두 번째 메시지", t2);
        createMessage("세 번째 메시지", t3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("채널 ID와 createdAt 기준 메시지 페이징 조회 성공")
    void findAllByChannelIdWithAuthor_success() {
        //given
        Instant cursor = now.plus(1, ChronoUnit.MINUTES);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        //when
        Slice<Message> slice = messageRepository.findAllByChannelIdWithAuthor(
            testChannel.getId(), cursor, pageable);

        //then
        assertThat(slice).isNotNull();
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.hasNext()).isTrue();

        assertThat(slice.getContent().get(0).getCreatedAt())
            .isAfterOrEqualTo(slice.getContent().get(1).getCreatedAt());

        Message firstMessage = slice.getContent().get(0);
        assertThat(Hibernate.isInitialized(firstMessage.getAuthor())).isTrue();
        assertThat(Hibernate.isInitialized(firstMessage.getAuthor().getStatus())).isTrue();
        assertThat(Hibernate.isInitialized(firstMessage.getAuthor().getProfile())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 채널에서 메시지 페이징 조회 시 빈 결과 반환")
    void findAllByChannelIdWithAuthor_notFound() {
        //given
        UUID invalidChannelId = UUID.randomUUID();
        Instant cursor = now.plus(1, ChronoUnit.MINUTES);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        //when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
            invalidChannelId, cursor, pageable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.hasContent()).isFalse();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("채널의 가장 최근 메시지 시간 조회 성공")
    void findLastMessageAtByChannelId_success() {
        //when
        Optional<Instant> last = messageRepository.findLastMessageAtByChannelId(
            testChannel.getId());

        //then
        assertThat(last).isPresent();
        assertThat(last.get().truncatedTo(ChronoUnit.SECONDS))
            .isEqualTo(t3.truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @DisplayName("존재하지 않는 채널에서 마지막 메시지 시간 조회 시 빈 Optional 반환")
    void findLastMessageAtByChannelId_notFound() {
        //given
        UUID invalidChannelId = UUID.randomUUID();

        //when
        Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(invalidChannelId);

        //then
        assertThat(result).isNotPresent();
    }
}