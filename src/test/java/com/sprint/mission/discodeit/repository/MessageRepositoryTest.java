package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")

@DataJpaTest
public class MessageRepositoryTest {

  @Autowired
  TestEntityManager em;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ChannelRepository channelRepository;

  @Autowired
  MessageRepository messageRepository;

  @Autowired
  BinaryContentRepository binaryContentRepository;

  @Test
  @Transactional
  void 첨부파일_없는_메시지를_저장하고_조회할_수_있다() {
    // given
    User user = userRepository.save(
        UserFixture.createValidUser(BinaryContentFixture.createValid()));
    Channel channel = channelRepository.save(ChannelFixture.createPublic());

    Message message = Message.create("첨부파일 없는 메시지", user, channel);
    messageRepository.save(message);
    em.flush();
    em.clear();

    // when
    List<Message> messages = messageRepository.findAll();

    // then
    assertThat(messages).hasSize(1);
    Message found = messages.get(0);
    assertThat(found.getContent()).isEqualTo("첨부파일 없는 메시지");
    assertThat(found.getAuthor().getId()).isEqualTo(user.getId());
    assertThat(found.getChannel().getId()).isEqualTo(channel.getId());
    assertThat(found.getAttachments()).isEmpty();
  }

  @Test
  @Transactional
  void 첨부파일이_포함된_메시지를_저장하고_조회할_수_있다() {
    // given
    User user = userRepository.save(
        UserFixture.createValidUser(BinaryContentFixture.createValid()));
    Channel channel = channelRepository.save(ChannelFixture.createPublic());

    Message message = Message.create("첨부파일 포함 메시지", user, channel);
    BinaryContent attachment1 = binaryContentRepository.save(BinaryContentFixture.createValid());
    BinaryContent attachment2 = binaryContentRepository.save(BinaryContentFixture.createValid());
    message.attach(attachment1);
    message.attach(attachment2);
    messageRepository.save(message);
    em.flush();
    em.clear();

    // when
    List<Message> messages = messageRepository.findAll();

    // then
    assertThat(messages).hasSize(1);
    Message found = messages.get(0);
    assertThat(found.getContent()).isEqualTo("첨부파일 포함 메시지");
    assertThat(found.getAuthor().getId()).isEqualTo(user.getId());
    assertThat(found.getChannel().getId()).isEqualTo(channel.getId());
    assertThat(found.getAttachments()).hasSize(2);
  }

  @Test
  void 채널별_메시지_목록_조회_성공() {
    // given
    User user = userRepository.save(
        UserFixture.createValidUser(BinaryContentFixture.createValid()));
    Channel channel = channelRepository.save(ChannelFixture.createPublic());
    messageRepository.save(Message.create("msg1", user, channel));
    messageRepository.save(Message.create("msg2", user, channel));
    em.flush();
    em.clear();

    // when
    var messages = messageRepository.findAllByChannelId(channel.getId());

    // then
    assertThat(messages.size()).isEqualTo(2);
  }

  @Test
  void 채널별_메시지_목록_조회_실패_채널없음() {
    // when
    var messages = messageRepository.findAllByChannelId(UUID.randomUUID());
    // then
    assertThat(messages.size()).isEqualTo(0);
  }

  @Test
  void 채널별_메시지_아이디_목록_조회() {
    // given
    User user = userRepository.save(
        UserFixture.createValidUser(BinaryContentFixture.createValid()));
    Channel channel = channelRepository.save(ChannelFixture.createPublic());
    Message m1 = messageRepository.save(Message.create("msg1", user, channel));
    Message m2 = messageRepository.save(Message.create("msg2", user, channel));
    em.flush();
    em.clear();
    // when
    var ids = messageRepository.findMessageIdsByChannelId(channel.getId());
    // then
    assertThat(ids).containsExactlyInAnyOrder(m1.getId(), m2.getId());
  }

  @Test
  void 채널별_가장_최근_메시지_생성일_조회() {
    // given
    User user = userRepository.save(
        UserFixture.createValidUser(BinaryContentFixture.createValid()));
    Channel channel = channelRepository.save(ChannelFixture.createPublic());
    messageRepository.save(Message.create("msg1", user, channel));
    messageRepository.save(Message.create("msg2", user, channel));
    em.flush();
    em.clear();
    // when
    var lastCreatedAt = messageRepository.findLastCreatedAtByChannelId(channel.getId());
    // then
    assertThat(lastCreatedAt).isNotNull();
  }

  @Test
  void 채널별_가장_최근_메시지_생성일_조회_실패() {
    // when
    var lastCreatedAt = messageRepository.findLastCreatedAtByChannelId(UUID.randomUUID());
    // then
    assertThat(lastCreatedAt).isNull();
  }

  @Test
  void 채널_페이징_정렬_조회_성공() {
    // given
    User user = userRepository.save(
        UserFixture.createValidUser(BinaryContentFixture.createValid()));
    Channel channel = channelRepository.save(ChannelFixture.createPublic());
    for (int i = 0; i < 5; i++) {
      messageRepository.save(Message.create("msg" + i, user, channel));
    }
    em.flush();
    em.clear();
    var now = java.time.Instant.now().plusSeconds(10); // 미래 시점
    // when
    var page = messageRepository.findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(
        channel.getId(), now, org.springframework.data.domain.PageRequest.of(0, 3));
    // then
    assertThat(page.getContent().size()).isEqualTo(3);
    assertThat(page.getTotalElements()).isEqualTo(5);
  }

  @Test
  void 채널_페이징_정렬_조회_실패_존재하지않는채널() {
    // when
    var page = messageRepository.findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(
        UUID.randomUUID(), java.time.Instant.now(),
        org.springframework.data.domain.PageRequest.of(0, 3));
    // then
    assertThat(page.getContent().size()).isEqualTo(0);
  }
}
