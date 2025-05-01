package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

public class ReadStatusFixture {

  /**
   * ReadStatus를 생성한다 (Request 기반)
   */
  public static ReadStatus createReadStatus(ReadStatusCreateRequest dto) {
    return ReadStatus.create(dto.userId(), dto.channelId());
  }

  /**
   * ReadStatus를 생성한다 (직접 ID 전달)
   */
  public static ReadStatus createReadStatusByIds(java.util.UUID userId, java.util.UUID channelId) {
    return ReadStatus.create(userId, channelId);
  }

  /**
   * 기본 ReadStatus를 생성한다. 유저와 채널도 함께 생성되며, 유저는 채널의 참여자로 등록된다.
   */
  public static ReadStatus createValidReadStatus() {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createCustomChannelWithParticipant(user,
        ChannelFixture.DEFAULT_CHANNEL_NAME, ChannelFixture.DEFAULT_CHANNEL_DESCRIPTION);
    return ReadStatus.create(user.getId(), channel.getId());
  }
}
