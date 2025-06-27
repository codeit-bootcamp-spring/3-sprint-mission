package com.sprint.mission.discodeit.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
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

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@DisplayName("MessageRepository 테스트")
class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private Channel testChannel;
    private User testUser;
    private Message message1;
    private Message message2;
    private Message message3;
    private Instant baseTime;

    @BeforeEach
    void setUp() {
        baseTime = Instant.now();

        // User 생성
        testUser = new User("testuser", "test@example.com", "password", null);

        // UserStatus 생성 및 연관관계 설정
        UserStatus userStatus = new UserStatus(testUser, Instant.now());

        // Channel 생성
        testChannel = new Channel(ChannelType.PUBLIC, "Test Channel", "Test channel description");

        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(testChannel);

        // Message 생성 (시간 순서대로)
        message1 = new Message("First message", testChannel, testUser, Arrays.asList());
        message2 = new Message("Second message", testChannel, testUser, Arrays.asList());
        message3 = new Message("Third message", testChannel, testUser, Arrays.asList());

        entityManager.persistAndFlush(message1);
        entityManager.persistAndFlush(message2);
        entityManager.persistAndFlush(message3);

        // 생성 시간 수정  - 커서페이지네이션 테스트를 위해서
        entityManager.getEntityManager()
            .createQuery("UPDATE Message m SET m.createdAt = :time1 WHERE m.id = :id1")
            .setParameter("time1", baseTime.minus(2, ChronoUnit.HOURS))
            .setParameter("id1", message1.getId())
            .executeUpdate();

        entityManager.getEntityManager()
            .createQuery("UPDATE Message m SET m.createdAt = :time2 WHERE m.id = :id2")
            .setParameter("time2", baseTime.minus(1, ChronoUnit.HOURS))
            .setParameter("id2", message2.getId())
            .executeUpdate();

        entityManager.getEntityManager()
            .createQuery("UPDATE Message m SET m.createdAt = :time3 WHERE m.id = :id3")
            .setParameter("time3", baseTime)
            .setParameter("id3", message3.getId())
            .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("채널 ID와 작성자 정보로 메시지 조회 - 성공")
    void findAllByChannelIdWithAuthor_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Instant cursor = baseTime.plus(1, ChronoUnit.HOURS); // 모든 메시지보다 미래 시간

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
            testChannel.getId(), cursor, pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting(Message::getContent)
            .containsExactly("Third message", "Second message", "First message"); // DESC 정렬

    }

    @Test
    @DisplayName("채널 ID와 작성자 정보로 메시지 조회 - 커서 기반 페이징")
    void findAllByChannelIdWithAuthor_WithCursor() {
        // given
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        Instant cursor = baseTime.minus(30, ChronoUnit.MINUTES); // message2와 message1만 조회되도록

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
            testChannel.getId(), cursor, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Message::getContent)
            .containsExactly("Second message", "First message");
    }

    @Test
    @DisplayName("채널의 마지막 메시지 시간 조회 - 성공")
    void findLastMessageAtByChannelId_Success() {
        // when
        Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(
            testChannel.getId());

        // then
        assertThat(result).isPresent();
        // 가장 최근 메시지(message3)의 시간이 반환되어야 함
        assertThat(result.get()).isAfter(baseTime.minus(1, ChronoUnit.MINUTES));
    }

    @Test
    @DisplayName("채널의 마지막 메시지 시간 조회 - 빈 결과 (메시지가 없는 채널)")
    void findLastMessageAtByChannelId_Empty() {
        // given
        Channel emptyChannel = new Channel(ChannelType.PUBLIC, "Empty Channel",
            "Channel with no messages");
        entityManager.persistAndFlush(emptyChannel);

        // when
        Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(
            emptyChannel.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 ID로 모든 메시지 삭제 - 성공")
    void deleteAllByChannelId_Success() {
        // given
        long initialCount = messageRepository.count();
        assertThat(initialCount).isEqualTo(3);

        // when
        messageRepository.deleteAllByChannelId(testChannel.getId());
        entityManager.flush();

        // then
        long finalCount = messageRepository.count();
        assertThat(finalCount).isEqualTo(0);
    }

    @Test
    @DisplayName("채널 ID로 모든 메시지 삭제 - 존재하지 않는 채널")
    void deleteAllByChannelId_NonExistentChannel() {
        // given
        long initialCount = messageRepository.count();
        assertThat(initialCount).isEqualTo(3);

        // when
        messageRepository.deleteAllByChannelId(UUID.randomUUID());
        entityManager.flush();

        // then
        long finalCount = messageRepository.count();
        assertThat(finalCount).isEqualTo(3); // 변화 없음
    }
}
