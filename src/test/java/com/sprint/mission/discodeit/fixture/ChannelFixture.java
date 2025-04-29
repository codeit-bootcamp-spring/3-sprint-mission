package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

/**
 * Channel 테스트를 위한 설비
 */
public class ChannelFixture {

  public static final String DEFAULT_CHANNEL_NAME = "테스트 채널";
  public static final String DEFAULT_CHANNEL_DESCRIPTION = "테스트 채널쓰임다";

  public static Channel createValidChannel() {
    User creator = UserFixture.createValidUser();
    return Channel.create(creator, DEFAULT_CHANNEL_NAME, DEFAULT_CHANNEL_DESCRIPTION);
  }

  public static Channel createCustomChannel(User creator, String name, String description) {
    return Channel.create(creator, name, description);
  }

  public static Channel createCustomPublicChannel(ChannelCreateRequest dto) {
    return Channel.createPublic(dto.creator(), dto.name(), dto.description());
  }


  public static Channel createCustomPrivateChannel(ChannelCreateRequest dto) {
    return Channel.createPrivate(dto.creator(), dto.name(), dto.description());
  }
}