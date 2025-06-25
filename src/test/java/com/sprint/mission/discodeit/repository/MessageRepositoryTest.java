package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@EnableJpaAuditing
@DataJpaTest
@DisplayName("MessageRepository 슬라이스 테스트")
public class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Test
  @DisplayName("해당 채널의 마지막 메세지 시간 가져오기")
  void whenLastMessageAtFindWithChannelId_thenReturnInstantTime() {
    // Given  -- Message 초기 데이터 (public, private)
//    INSERT INTO messages (id, created_at, content, channel_id, author_id)
//    VALUES ('00000000-0000-0000-0000-000000000040', now(), 'Welcome to the general channel!',
//        '00000000-0000-0000-0000-000000000030', '00000000-0000-0000-0000-000000000010'),
//        ('00000000-0000-0000-0000-000000000041', now(), 'Hello team!',
//        '00000000-0000-0000-0000-000000000031', '00000000-0000-0000-0000-000000000011');
    UUID channelId = UUID.fromString("00000000-0000-0000-0000-000000000030");

    // When
    Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(channelId);

    // Then

    assertNotNull(lastMessageAt);

  }

  @DisplayName("메시지를 채널 ID, createdAt 기준으로 페이징 조회 (author, status, profile 포함)")
  @Test
  void whenFindAllByChannelIdWithAuthor_thenReturnSlice() {
    // Given
    UUID channelId = UUID.fromString("00000000-0000-0000-0000-000000000030");
    Instant now = Instant.now();
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    // When
    Slice<Message> messages = messageRepository.findAllByChannelIdWithAuthor(channelId, now,
        pageable);

    // Then
    assertFalse(messages.isEmpty(), "조회된 메시지가 있어야 함");
    Message first = messages.getContent().get(0);
    assertNotNull(first.getAuthor(), "작성자(author)가 lazy loading 없이 로딩되어야 함");
    assertNotNull(first.getAuthor().getStatus(), "작성자의 상태(status)가 로딩되어야 함");
    // 프로필은 nullable이므로 null만 아니면 검증 가능
    assertNotNull(first.getAuthor().getProfile(), "작성자의 프로필이 fetch되어야 함");
  }
}
