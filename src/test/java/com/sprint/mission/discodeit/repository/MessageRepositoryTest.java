package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestQuerydslConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MessageRepository 슬라이스 테스트")
@EnableJpaAuditing
@Import(TestQuerydslConfig.class)
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
    @DisplayName("커서 없이 DESC 정렬 조회 시 최신순 limit만큼 반환")
    void findByChannelIdWithCursor_Desc_NoCursor() {
        // Given
        UUID channelId = testChannel.getId();

        // When
        List<Message> result = messageRepository.findByChannelIdWithCursor(
            channelId, null, Sort.Direction.DESC, 2
        );

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(Message::getContent)
            .containsExactly("세 번째 메시지", "두 번째 메시지");
    }

    @Test
    @DisplayName("커서 지정 후 DESC 정렬 조회 시 커서보다 이전 데이터만 반환")
    void findByChannelIdWithCursor_Desc_WithCursor() {
        // Given
        UUID channelId = testChannel.getId();
        Instant cursor = testMessage3.getCreatedAt();

        // When
        List<Message> result = messageRepository.findByChannelIdWithCursor(
            channelId, cursor, Sort.Direction.DESC, 2
        );

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(Message::getContent)
            .containsExactly("두 번째 메시지", "첫 번째 메시지");
    }

    @Test
    @DisplayName("커서 없이 ASC 정렬 조회 시 오래된 순으로 limit만큼 반환")
    void findByChannelIdWithCursor_Asc_NoCursor() {
        // Given
        UUID channelId = testChannel.getId();
        Instant cursor = entityManager.find(Message.class, testMessage1.getId())
            .getCreatedAt()
            .truncatedTo(ChronoUnit.MILLIS);

        // When
        List<Message> result = messageRepository.findByChannelIdWithCursor(
            channelId, cursor, Sort.Direction.ASC, 2
        );

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(Message::getContent)
            .containsExactly("첫 번째 메시지", "두 번째 메시지");
    }

    @Test
    @DisplayName("존재하지 않는 채널 ID 조회 시 빈 리스트 반환")
    void findByChannelIdWithCursor_NoChannel() {
        // Given
        UUID nonExistentChannelId = UUID.randomUUID();

        // When
        List<Message> result = messageRepository.findByChannelIdWithCursor(
            nonExistentChannelId, null, Sort.Direction.DESC, 2
        );

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널의 모든 메시지 삭제 - 성공")
    void deleteAllByChannelId_Success() {
        // Given
        UUID channelId = testChannel.getId();

        // When
        messageRepository.deleteAllByChannelId(channelId);
        entityManager.flush();
        entityManager.clear();

        // Then
        Slice<Message> result = messageRepository.findByChannelIdAndCreatedAtLessThan(
            channelId,
            Instant.now(),
            PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).isEmpty();
    }
}