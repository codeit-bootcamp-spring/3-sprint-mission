package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MessageRepository 슬라이스 테스트")
@Import(AppConfig.class)
class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private User testUser;
    private Channel testChannel;
    private Message testMessage1;
    private Message testMessage2;
    private Message testMessage3;

    @BeforeAll
    static void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeEach
    void setUp() {
        Instant baseTime = Instant.parse("2025-07-07T09:00:00Z");

        // 테스트 데이터 생성
        testUser = new User("테스트사용자", "test@email.com", "password123!", null);
        testUser = entityManager.persistAndFlush(testUser);

        testChannel = new Channel(ChannelType.PUBLIC, "테스트채널", "테스트 채널입니다");
        testChannel = entityManager.persistAndFlush(testChannel);

        testMessage1 = new Message("첫 번째 메시지", testChannel, testUser, null);
        ReflectionTestUtils.setField(testMessage1, "createdAt", baseTime);
        testMessage1 = entityManager.persistAndFlush(testMessage1);

        testMessage2 = new Message("두 번째 메시지", testChannel, testUser, null);
        ReflectionTestUtils.setField(testMessage2, "createdAt", baseTime.plusSeconds(60));
        testMessage2 = entityManager.persistAndFlush(testMessage2);

        testMessage3 = new Message("세 번째 메시지", testChannel, testUser, null);
        ReflectionTestUtils.setField(testMessage3, "createdAt", baseTime.plusSeconds(120));
        testMessage3 = entityManager.persistAndFlush(testMessage3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("채널 ID로 메시지 조회 - 성공")
    void findByChannelIdAndCreatedAtLessThan_Success() {
        // Given
        Instant currentTime = Instant.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // When
        Slice<Message> result = messageRepository.findByChannelIdAndCreatedAtLessThan(
            testChannel.getId(), currentTime, pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
            .extracting(Message::getContent)
            .containsExactly("세 번째 메시지", "두 번째 메시지", "첫 번째 메시지"); // 최신순 정렬
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("채널 ID로 메시지 조회 - 페이징 테스트(size=2로 가정)")
    void findByChannelIdAndCreatedAtLessThan_WithPaging() {
        // Given
        Instant currentTime = Instant.now();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        // When
        Slice<Message> result = messageRepository.findByChannelIdAndCreatedAtLessThan(
            testChannel.getId(), currentTime, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isTrue(); // 다음 페이지 존재
        assertThat(result.getContent())
            .extracting(Message::getContent)
            .containsExactly("세 번째 메시지", "두 번째 메시지");
    }

    @Test
    @DisplayName("채널 ID로 메시지 조회 - 존재하지 않는 채널")
    void findByChannelIdAndCreatedAtLessThan_ChannelNotExists() {
        // Given
        Instant currentTime = Instant.now();
        UUID nonExistentChannelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Slice<Message> result = messageRepository.findByChannelIdAndCreatedAtLessThan(
            nonExistentChannelId, currentTime, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("채널의 마지막 메시지 시간 조회 - 성공")
    void findLastMessageTimeByChannelId_Success() {
        // When
        Optional<Message> lastMessage = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(
            testChannel.getId());

        // Then
        assertThat(lastMessage).isPresent();
        Instant actual = lastMessage.get().getCreatedAt().truncatedTo(ChronoUnit.MILLIS);
        Instant expected = testMessage3.getCreatedAt().truncatedTo(ChronoUnit.MILLIS);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("채널의 마지막 메시지 시간 조회 - 메시지가 없는 채널")
    void findLastMessageTimeByChannelId_NoMessages() {
        // Given
        Channel emptyChannel = new Channel(ChannelType.PRIVATE, "빈 채널", null);
        emptyChannel = entityManager.persistAndFlush(emptyChannel);

        // When
        Optional<Message> lastMessage = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(
            emptyChannel.getId());

        // Then
        assertThat(lastMessage).isNotPresent();
    }

    @Test
    @DisplayName("채널의 모든 메시지 삭제 - 성공")
    void deleteAllByChannelId_Success() {
        // When
        messageRepository.deleteAllByChannelId(testChannel.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        Slice<Message> result = messageRepository.findByChannelIdAndCreatedAtLessThan(
            testChannel.getId(),
            Instant.now(),
            PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).isEmpty();
    }
}