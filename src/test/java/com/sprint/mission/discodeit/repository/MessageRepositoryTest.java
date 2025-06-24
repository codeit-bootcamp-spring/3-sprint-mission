package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.TestJpaAuditingConfig;
import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
@DataJpaTest
@EntityScan(basePackages = "com.sprint.mission.discodeit.entity")
@DisplayName("MessageRepository 슬라이스 테스트")
public class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager em;

    private User user;

    private Channel channel;

    @BeforeEach
    void setUp() {
        // 유저와 연관 엔티티 저장
        BinaryContent profile = new BinaryContent("profile.jpg", 123L, "jpg");
        em.persist(profile);

        user = new User("tester", "tester@example.com", "pw1234", profile);
        em.persist(user);

        UserStatus status = new UserStatus(user, Instant.now());
        em.persist(status);

        channel = new Channel(ChannelType.PUBLIC, "general", "일반 채널");
        em.persist(channel);

        System.out.println("셋업" + channel.getId());

        // 메시지 3개 생성
        for (int i = 0; i < 3; i++) {
            Message message = new Message("내용 " + i, channel, user);
            em.persist(message);
            em.flush();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        em.clear();
    }

    @BeforeAll
    static void beforeAll() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    void save() {
        // given
        Message message = new Message("test", channel, user);
        // when
        Message result = messageRepository.save(message);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("test");
    }

    @Test
    @DisplayName("채널 ID로 최신순 메시지 목록을 조회할 수 있다")
    void findByChannelIdOrderByCreatedAtDesc() {
        // when
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Message> result = messageRepository.findByChannelIdOrderByCreatedAtDesc(channel.getId(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("내용 2");
    }

    @Test
    @DisplayName("커서 기반으로 이전 메시지를 조회할 수 있다")
    void findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc() {
        // given
        UUID channelId = channel.getId();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<Message> messages = messageRepository
                .findByChannelIdOrderByCreatedAtDesc(channelId, pageable)
                .getContent();

        Message cursorTarget = messages.get(0);
        Instant cursor = cursorTarget.getCreatedAt();
        cursor = cursor.atOffset(ZoneOffset.UTC).toInstant();

        // when
        Slice<Message> result = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
                channelId, cursor, pageable);


        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("내용 1");
    }

    @Test
    @DisplayName("채널 ID로 모든 메시지를 삭제할 수 있다")
    void deleteAllByChannelId() {
        // when
        messageRepository.deleteAllByChannelId(channel.getId());

        em.flush();
        em.clear();

        // then
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Message> result = messageRepository.findByChannelIdOrderByCreatedAtDesc(channel.getId(), pageable);

        assertThat(result.getContent()).isEmpty();
    }
}
