package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
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
}
