package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class MessageFixture {

  public static final String DEFAULT_MESSAGE_CONTENT = "테스트 메시지입니다.";
  private static final Set<BinaryContent> DEFAULT_ATTACHMENTS = new HashSet<>();

  public static Message createValid() {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPublic();
    Message message = Message.create(
        DEFAULT_MESSAGE_CONTENT,
        user,
        channel
    );
    message.assignCreatedAtForTest(Instant.now());
    return message;
  }

  public static Message createCustom(
      String content,
      User author,
      Channel channel
  ) {
    Message message = Message.create(
        content,
        author,
        channel
    );
    message.assignCreatedAtForTest(Instant.now());
    return message;
  }

  public static Message createWithAttachments(
      String content,
      User author,
      Channel channel,
      Set<BinaryContent> attachments
  ) {
    Message message = Message.create(content, author, channel);
    attachments.forEach(message::attach);
    return message;
  }
}
