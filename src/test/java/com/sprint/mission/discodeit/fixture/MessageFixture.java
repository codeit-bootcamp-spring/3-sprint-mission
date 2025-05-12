package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MessageFixture {

  public static final String DEFAULT_MESSAGE_CONTENT = "테스트 메시지입니다.";
  private static final Set<UUID> DEFAULT_ATTACHMENT_IDS = new HashSet<>();

  public static Message createValid() {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPublic();
    return Message.create(DEFAULT_MESSAGE_CONTENT, user.getId(), channel.getId(),
        DEFAULT_ATTACHMENT_IDS);
  }

  public static Message createCustom(MessageCreateRequest dto) {
    return Message.create(dto.content(), dto.userId(), dto.channelId(), DEFAULT_ATTACHMENT_IDS);
  }

  public static Message createCustomWithAttachments(MessageCreateRequest messageCreateRequest,
      List<UUID> attachmentIds) {
    return Message.create(
        messageCreateRequest.content(),
        messageCreateRequest.userId(),
        messageCreateRequest.channelId(),
        new HashSet<>(attachmentIds)
    );
  }
}
