package com.sprint.mission.discodeit.service.channel;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ChannelServiceDeleteIntegrationTest {

  @Autowired
  ChannelService channelService;
  @Autowired
  ChannelRepository channelRepository;
  @Autowired
  MessageRepository messageRepository;
  @Autowired
  ReadStatusRepository readStatusRepository;
  @Autowired
  MessageAttachmentRepository messageAttachmentRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  BinaryContentRepository binaryContentRepository;

  @Test
  void 채널_삭제_시_관련_데이터도_함께_삭제된다() {
    // given
    User user = userRepository.save(UserFixture.createValidUser());
    Channel channel = channelRepository.save(Channel.createPublic("test", "desc"));
    BinaryContent binary = binaryContentRepository.save(BinaryContentFixture.createValid());

    Message message = MessageFixture.createWithAttachments(
        "첨부 포함 메시지",
        user,
        channel,
        Set.of(binary)
    );

    messageRepository.save(message); // cascade 로 attachments 자동 저장

    UUID channelId = channel.getId();

    // when
    channelService.delete(channelId);

    // then
    assertThat(channelRepository.findById(channelId)).isEmpty();
    assertThat(messageRepository.findAllByChannelId(channelId)).isEmpty();
    assertThat(messageAttachmentRepository.findAll()).isEmpty(); // orphanRemoval 작동 검증
  }
}

