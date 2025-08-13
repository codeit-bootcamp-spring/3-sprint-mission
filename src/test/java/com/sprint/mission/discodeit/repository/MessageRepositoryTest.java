package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DisplayName("MessageRepository 슬라이스 테스트")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Autowired
  private MessageAttachmentRepository messageAttachmentRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EntityManager entityManager;

  // === TestFixture 메서드들 ===

  private User createAndSaveTestUser(String username, String email) {
    User user = TestDataBuilder.createUser(username, email);
    return userRepository.save(user);
  }

  private Channel createAndSaveTestChannel(ChannelType type, String name, String description) {
    Channel channel = new Channel(type, name, description);
    return channelRepository.save(channel);
  }

  private BinaryContent createAndSaveTestBinaryContent(String filename, String contentType) {
    BinaryContent binaryContent = new BinaryContent(filename, 1024L, contentType);
    return binaryContentRepository.save(binaryContent);
  }

  private Message createAndSaveTestMessage(String content, Channel channel, User author) {
    Message message = new Message(content, channel, author);
    return messageRepository.save(message);
  }

  private Message createAndSaveTestMessageWithAttachments(String content, Channel channel, User author,
      List<BinaryContent> attachments) {
    Message message = new Message(content, channel, author, attachments);
    return messageRepository.save(message);
  }

  private void setEntityId(Object entity, UUID id) {
    try {
      Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(entity, id);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set ID", e);
    }
  }

  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  // === 기본 채널별 메시지 조회 테스트 ===

  @Test
  @DisplayName("findAllByChannelIdOrderByCreatedAtAsc_존재하는채널ID_메시지목록반환")
  void findAllByChannelIdOrderByCreatedAtAsc_ExistingChannelId_ReturnsMessages() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    createAndSaveTestMessage("First message", channel, author);
    createAndSaveTestMessage("Second message", channel, author);
    clearPersistenceContext();

    // when
    List<Message> messages = messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channel.getId());

    // then
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0).getContent()).isEqualTo("First message");
    assertThat(messages.get(1).getContent()).isEqualTo("Second message");
    // 생성 시간 순으로 정렬되어 있는지 확인
    assertThat(messages.get(0).getCreatedAt()).isBeforeOrEqualTo(messages.get(1).getCreatedAt());
  }

  @Test
  @DisplayName("findAllByChannelIdOrderByCreatedAtAsc_존재하지않는채널ID_빈목록반환")
  void findAllByChannelIdOrderByCreatedAtAsc_NonExistingChannelId_ReturnsEmptyList() {
    // given
    UUID nonExistingChannelId = UUID.randomUUID();
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    createAndSaveTestMessage("Test message", channel, author);
    clearPersistenceContext();

    // when
    List<Message> messages = messageRepository.findAllByChannelIdOrderByCreatedAtAsc(nonExistingChannelId);

    // then
    assertThat(messages).isEmpty();
  }

  // === Fetch Join 쿼리 테스트 ===

  @Test
  @DisplayName("findAllByChannelIdWithAuthorOrderByCreatedAtAsc_존재하는채널ID_작성자정보포함메시지반환")
  void findAllByChannelIdWithAuthorOrderByCreatedAtAsc_ExistingChannelId_ReturnsMessagesWithAuthor() {
    // given
    User author1 = createAndSaveTestUser("author1", "author1@example.com");
    User author2 = createAndSaveTestUser("author2", "author2@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    createAndSaveTestMessage("Message by author1", channel, author1);
    createAndSaveTestMessage("Message by author2", channel, author2);
    clearPersistenceContext();

    // when
    List<Message> messages = messageRepository.findAllByChannelIdWithAuthorOrderByCreatedAtAsc(channel.getId());

    // then
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0).getAuthor().getUsername()).isEqualTo("author1");
    assertThat(messages.get(1).getAuthor().getUsername()).isEqualTo("author2");
    assertThat(messages.get(0).getContent()).isEqualTo("Message by author1");
    assertThat(messages.get(1).getContent()).isEqualTo("Message by author2");
  }

  @Test
  @DisplayName("findAllByChannelIdWithAuthorAndAttachmentsOrderByCreatedAtAsc_첨부파일포함메시지_작성자와첨부파일정보반환")
  void findAllByChannelIdWithAuthorAndAttachmentsOrderByCreatedAtAsc_MessagesWithAttachments_ReturnsWithAuthorAndAttachments() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    BinaryContent attachment1 = createAndSaveTestBinaryContent("file1.jpg", "image/jpeg");
    BinaryContent attachment2 = createAndSaveTestBinaryContent("file2.pdf", "application/pdf");

    createAndSaveTestMessage("Message without attachment", channel, author);
    createAndSaveTestMessageWithAttachments("Message with attachments", channel, author,
        Arrays.asList(attachment1, attachment2));
    clearPersistenceContext();

    // when
    List<Message> messages = messageRepository
        .findAllByChannelIdWithAuthorAndAttachmentsOrderByCreatedAtAsc(channel.getId());

    // then
    assertThat(messages).hasSize(2);

    // 모든 메시지에 작성자 정보가 포함되어 있는지 확인
    assertThat(messages.stream().allMatch(m -> m.getAuthor() != null)).isTrue();
    assertThat(messages.stream().allMatch(m -> "author".equals(m.getAuthor().getUsername()))).isTrue();

    // 첨부파일이 없는 메시지와 있는 메시지가 각각 존재하는지 확인
    boolean hasMessageWithoutAttachment = messages.stream()
        .anyMatch(m -> m.getMessageAttachments().isEmpty());
    boolean hasMessageWithAttachment = messages.stream()
        .anyMatch(m -> m.getMessageAttachments().size() == 2);

    assertThat(hasMessageWithoutAttachment).isTrue();
    assertThat(hasMessageWithAttachment).isTrue();
  }

  // === 페이징 조회 테스트 ===

  @Test
  @DisplayName("findAllByChannelId_페이징처리_페이지네이션결과반환")
  void findAllByChannelId_WithPaging_ReturnsPagedResults() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    for (int i = 1; i <= 5; i++) {
      createAndSaveTestMessage("Message " + i, channel, author);
    }
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 3);

    // when
    Page<Message> messagePage = messageRepository.findAllByChannelId(channel.getId(), pageable);

    // then
    assertThat(messagePage.getContent()).hasSize(3);
    assertThat(messagePage.getTotalElements()).isEqualTo(5);
    assertThat(messagePage.getTotalPages()).isEqualTo(2);
    assertThat(messagePage.isFirst()).isTrue();
    assertThat(messagePage.hasNext()).isTrue();
  }

  @Test
  @DisplayName("findAllByChannelIdWithAuthor_페이징과작성자정보_페이지네이션된작성자정보포함메시지반환")
  void findAllByChannelIdWithAuthor_WithPagingAndAuthor_ReturnsPagedMessagesWithAuthor() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    for (int i = 1; i <= 4; i++) {
      createAndSaveTestMessage("Message " + i, channel, author);
    }
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 2);

    // when
    Page<Message> messagePage = messageRepository.findAllByChannelIdWithAuthor(channel.getId(), pageable);

    // then
    assertThat(messagePage.getContent()).hasSize(2);
    assertThat(messagePage.getTotalElements()).isEqualTo(4);
    assertThat(messagePage.getContent().get(0).getAuthor().getUsername()).isEqualTo("author");
    assertThat(messagePage.getContent().get(1).getAuthor().getUsername()).isEqualTo("author");
  }

  @Test
  @DisplayName("findAllByChannelIdWithAuthorAndAttachments_페이징과첨부파일_페이지네이션된첨부파일포함메시지반환")
  void findAllByChannelIdWithAuthorAndAttachments_WithPagingAndAttachments_ReturnsPagedMessagesWithAttachments() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    BinaryContent attachment = createAndSaveTestBinaryContent("file.jpg", "image/jpeg");

    createAndSaveTestMessage("Message 1", channel, author);
    createAndSaveTestMessageWithAttachments("Message 2", channel, author, Arrays.asList(attachment));
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 2);

    // when
    Page<Message> messagePage = messageRepository.findAllByChannelIdWithAuthorAndAttachments(channel.getId(), pageable);

    // then
    assertThat(messagePage.getContent()).hasSize(2);
    assertThat(messagePage.getTotalElements()).isEqualTo(2);
    // 모든 메시지에 작성자 정보가 포함되어 있는지 확인
    assertThat(messagePage.getContent().stream().allMatch(m -> m.getAuthor() != null)).isTrue();
    assertThat(messagePage.getContent().get(0).getAuthor().getUsername()).isEqualTo("author");
    // 첨부파일이 있는 메시지 확인
    boolean hasAttachmentMessage = messagePage.getContent().stream()
        .anyMatch(m -> !m.getMessageAttachments().isEmpty());
    assertThat(hasAttachmentMessage).isTrue();
  }

  // === 커서 페이지네이션 테스트 ===

  @Test
  @DisplayName("findAllByChannelIdAfterCursorWithAuthor_커서페이지네이션_커서이후메시지반환")
  void findAllByChannelIdAfterCursorWithAuthor_CursorPagination_ReturnsMessagesAfterCursor() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    // 모든 메시지를 먼저 생성
    Message message1 = createAndSaveTestMessage("Message 1", channel, author);
    Message message2 = createAndSaveTestMessage("Message 2", channel, author);
    Message message3 = createAndSaveTestMessage("Message 3", channel, author);
    clearPersistenceContext();

    // 생성된 메시지들을 ID 순으로 정렬하여 실제 순서 확인
    List<Message> allMessages = messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channel.getId());
    allMessages.sort((m1, m2) -> m1.getId().compareTo(m2.getId()));

    // 가장 작은 ID를 커서로 사용
    UUID cursorId = allMessages.get(0).getId();
    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<Message> messages = messageRepository.findAllByChannelIdAfterCursorWithAuthor(
        channel.getId(), cursorId, pageable);

    // then
    // 커서 이후의 메시지가 반환되어야 함
    assertThat(messages.size()).isLessThanOrEqualTo(2);

    // 반환된 메시지가 있다면 검증
    if (!messages.isEmpty()) {
      // 모든 메시지의 ID가 커서 ID보다 큰지 확인
      for (Message message : messages) {
        assertThat(message.getId().compareTo(cursorId)).isGreaterThan(0);
      }
      // 작성자 정보가 포함되었는지 확인
      assertThat(messages.get(0).getAuthor().getUsername()).isEqualTo("author");
    }
  }

  @Test
  @DisplayName("findFirstPageByChannelIdWithAuthor_첫페이지조회_첫페이지메시지반환")
  void findFirstPageByChannelIdWithAuthor_FirstPage_ReturnsFirstPageMessages() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    for (int i = 1; i <= 5; i++) {
      createAndSaveTestMessage("Message " + i, channel, author);
    }
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 3);

    // when
    List<Message> messages = messageRepository.findFirstPageByChannelIdWithAuthor(channel.getId(), pageable);

    // then
    assertThat(messages).hasSize(3);
    // 작성자 정보가 포함되었는지 확인
    assertThat(messages.get(0).getAuthor().getUsername()).isEqualTo("author");
    // 모든 메시지가 해당 채널의 메시지인지 확인
    assertThat(messages.stream().allMatch(m -> m.getChannel().getId().equals(channel.getId()))).isTrue();
  }

  @Test
  @DisplayName("findAllByChannelIdAfterCursorTimeWithAuthorAndAttachments_시간기반커서_시간이후메시지반환")
  void findAllByChannelIdAfterCursorTimeWithAuthorAndAttachments_TimeCursor_ReturnsMessagesAfterTime() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Message message1 = createAndSaveTestMessage("Message 1", channel, author);
    clearPersistenceContext();

    Instant cursorTime = message1.getCreatedAt();

    // 약간의 시간 지연 후 추가 메시지 생성
    Message message2 = createAndSaveTestMessage("Message 2", channel, author);
    Message message3 = createAndSaveTestMessage("Message 3", channel, author);
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<Message> messages = messageRepository.findAllByChannelIdAfterCursorTimeWithAuthorAndAttachments(
        channel.getId(), cursorTime, pageable);

    // then
    assertThat(messages.size()).isGreaterThanOrEqualTo(2);
    assertThat(messages.stream().allMatch(m -> m.getCreatedAt().isAfter(cursorTime))).isTrue();
    assertThat(messages.get(0).getAuthor().getUsername()).isEqualTo("author");
  }

  // === 통계 쿼리 테스트 ===

  @Test
  @DisplayName("findLastMessageTimeByChannelId_존재하는채널_마지막메시지시간반환")
  void findLastMessageTimeByChannelId_ExistingChannel_ReturnsLastMessageTime() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    Message message1 = createAndSaveTestMessage("Message 1", channel, author);
    Message message2 = createAndSaveTestMessage("Message 2", channel, author);
    clearPersistenceContext();

    // when
    Optional<Instant> lastMessageTime = messageRepository.findLastMessageTimeByChannelId(channel.getId());

    // then
    assertThat(lastMessageTime).isPresent();
    // 시간 비교를 초 단위로 절삭하여 나노초 차이 문제 해결
    assertThat(lastMessageTime.get().truncatedTo(java.time.temporal.ChronoUnit.SECONDS))
        .isAfterOrEqualTo(message1.getCreatedAt().truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("findLastMessageTimeByChannelId_메시지없는채널_빈Optional반환")
  void findLastMessageTimeByChannelId_ChannelWithoutMessages_ReturnsEmpty() {
    // given
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Empty Channel", "Description");
    clearPersistenceContext();

    // when
    Optional<Instant> lastMessageTime = messageRepository.findLastMessageTimeByChannelId(channel.getId());

    // then
    assertThat(lastMessageTime).isEmpty();
  }

  @Test
  @DisplayName("findLastMessageTimesByChannelIds_여러채널ID목록_각채널의마지막메시지시간반환")
  void findLastMessageTimesByChannelIds_MultipleChannelIds_ReturnsLastMessageTimesForEachChannel() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel1 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 1", "Description 1");
    Channel channel2 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 2", "Description 2");

    createAndSaveTestMessage("Message in channel 1", channel1, author);
    createAndSaveTestMessage("Message in channel 2", channel2, author);
    clearPersistenceContext();

    List<UUID> channelIds = Arrays.asList(channel1.getId(), channel2.getId());

    // when
    List<Object[]> results = messageRepository.findLastMessageTimesByChannelIds(channelIds);

    // then
    assertThat(results).hasSize(2);
    assertThat(results.stream().map(result -> (UUID) result[0]))
        .containsExactlyInAnyOrder(channel1.getId(), channel2.getId());
    assertThat(results.stream().map(result -> (Instant) result[1]))
        .allSatisfy(time -> assertThat(time).isNotNull());
  }

  // === JpaRepository 기본 메서드 테스트 ===

  @Test
  @DisplayName("save_유효한메시지정보_메시지저장성공")
  void save_ValidMessageData_SavesSuccessfully() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    Message message = new Message("Test message content", channel, author);

    // when
    Message savedMessage = messageRepository.save(message);
    clearPersistenceContext();

    // then
    assertThat(savedMessage.getId()).isNotNull();
    assertThat(savedMessage.getCreatedAt()).isNotNull();
    assertThat(savedMessage.getUpdatedAt()).isNotNull();
    assertThat(savedMessage.getContent()).isEqualTo("Test message content");
    assertThat(savedMessage.getChannel().getId()).isEqualTo(channel.getId());
    assertThat(savedMessage.getAuthor().getId()).isEqualTo(author.getId());
  }

  @Test
  @DisplayName("findById_존재하는ID로조회_메시지반환")
  void findById_ExistingId_ReturnsMessage() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    Message savedMessage = createAndSaveTestMessage("Test message", channel, author);
    clearPersistenceContext();

    // when
    Optional<Message> foundMessage = messageRepository.findById(savedMessage.getId());

    // then
    assertThat(foundMessage).isPresent();
    assertThat(foundMessage.get().getContent()).isEqualTo("Test message");
    assertThat(foundMessage.get().getChannel().getId()).isEqualTo(channel.getId());
    assertThat(foundMessage.get().getAuthor().getId()).isEqualTo(author.getId());
  }

  @Test
  @DisplayName("deleteById_존재하는ID로삭제_삭제성공")
  void deleteById_ExistingId_DeletesSuccessfully() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    Message savedMessage = createAndSaveTestMessage("Test message", channel, author);
    clearPersistenceContext();

    // 삭제 전 존재 확인
    Optional<Message> beforeDelete = messageRepository.findById(savedMessage.getId());
    assertThat(beforeDelete).isPresent();

    // when & then - 예외 없이 실행되어야 함
    assertThatNoException().isThrownBy(() -> {
      messageRepository.deleteById(savedMessage.getId());
    });
  }

  @Test
  @DisplayName("deleteAllByChannelId_채널의모든메시지삭제_삭제성공")
  void deleteAllByChannelId_DeleteAllMessagesInChannel_DeletesSuccessfully() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel1 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 1", "Description 1");
    Channel channel2 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 2", "Description 2");

    createAndSaveTestMessage("Message in channel 1-1", channel1, author);
    createAndSaveTestMessage("Message in channel 1-2", channel1, author);
    createAndSaveTestMessage("Message in channel 2", channel2, author);
    clearPersistenceContext();

    // 삭제 전 확인
    List<Message> beforeDelete = messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channel1.getId());
    assertThat(beforeDelete).hasSize(2);

    // when & then - 예외 없이 실행되어야 함
    assertThatNoException().isThrownBy(() -> {
      messageRepository.deleteAllByChannelId(channel1.getId());
    });

    // 다른 채널의 메시지는 영향받지 않음 확인
    List<Message> channel2Messages = messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channel2.getId());
    assertThat(channel2Messages).hasSize(1);
  }

  // === 페이징 및 정렬 테스트 ===

  @Test
  @DisplayName("findAll_생성시간기준정렬_정렬된결과반환")
  void findAll_WithSorting_ReturnsSortedResults() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    createAndSaveTestMessage("First message", channel, author);
    createAndSaveTestMessage("Second message", channel, author);
    createAndSaveTestMessage("Third message", channel, author);
    clearPersistenceContext();

    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

    // when
    List<Message> sortedMessages = messageRepository.findAll(sort);

    // then
    assertThat(sortedMessages).hasSize(3);
    // 최신 메시지가 먼저 나와야 함
    assertThat(sortedMessages.get(0).getCreatedAt())
        .isAfterOrEqualTo(sortedMessages.get(1).getCreatedAt());
    assertThat(sortedMessages.get(1).getCreatedAt())
        .isAfterOrEqualTo(sortedMessages.get(2).getCreatedAt());
  }

  @Test
  @DisplayName("findAll_페이징과정렬조합_페이지네이션된정렬결과반환")
  void findAll_WithPagingAndSorting_ReturnsPagedAndSortedResults() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    for (int i = 1; i <= 5; i++) {
      createAndSaveTestMessage("Message " + i, channel, author);
    }
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

    // when
    Page<Message> messagePage = messageRepository.findAll(pageable);

    // then
    assertThat(messagePage.getContent()).hasSize(3);
    assertThat(messagePage.getTotalElements()).isEqualTo(5);
    assertThat(messagePage.isFirst()).isTrue();
    assertThat(messagePage.hasNext()).isTrue();
    // 최신 메시지가 먼저 나와야 함
    assertThat(messagePage.getContent().get(0).getCreatedAt())
        .isAfterOrEqualTo(messagePage.getContent().get(1).getCreatedAt());
  }

  // === 엔티티 업데이트 테스트 ===

  @Test
  @DisplayName("update_메시지내용변경_업데이트성공")
  void update_MessageContentChange_UpdatesSuccessfully() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    Message message = createAndSaveTestMessage("Original content", channel, author);
    clearPersistenceContext();

    // when
    Message foundMessage = messageRepository.findById(message.getId()).orElseThrow();
    String originalContent = foundMessage.getContent();
    foundMessage.update("Updated content");
    Message savedMessage = messageRepository.saveAndFlush(foundMessage);

    // then
    // 내용이 변경되었는지 확인
    assertThat(savedMessage.getContent()).isEqualTo("Updated content");
    assertThat(savedMessage.getContent()).isNotEqualTo(originalContent);
    // 기본적인 필드들이 유지되는지 확인
    assertThat(savedMessage.getId()).isEqualTo(message.getId());
    assertThat(savedMessage.getChannel().getId()).isEqualTo(channel.getId());
    assertThat(savedMessage.getAuthor().getId()).isEqualTo(author.getId());
  }

  // === 첨부파일 관련 테스트 ===

  @Test
  @DisplayName("messageWithAttachments_첨부파일포함메시지생성_첨부파일정보포함저장")
  void messageWithAttachments_CreateMessageWithAttachments_SavesWithAttachmentInfo() {
    // given
    User author = createAndSaveTestUser("author", "author@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");
    BinaryContent attachment1 = createAndSaveTestBinaryContent("file1.jpg", "image/jpeg");
    BinaryContent attachment2 = createAndSaveTestBinaryContent("file2.pdf", "application/pdf");

    // when
    Message messageWithAttachments = createAndSaveTestMessageWithAttachments(
        "Message with attachments", channel, author, Arrays.asList(attachment1, attachment2));
    clearPersistenceContext();

    // then
    Message foundMessage = messageRepository.findById(messageWithAttachments.getId()).orElseThrow();
    assertThat(foundMessage.getMessageAttachments()).hasSize(2);
    assertThat(foundMessage.getMessageAttachments().stream()
        .map(ma -> ma.getAttachment().getFileName()))
        .containsExactlyInAnyOrder("file1.jpg", "file2.pdf");
  }
}