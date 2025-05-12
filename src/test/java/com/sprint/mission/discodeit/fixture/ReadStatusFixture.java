package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

public class ReadStatusFixture {

  /**
   * 기본 유효한 ReadStatus 생성
   */
  public static ReadStatus create() {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPrivate();
    return ReadStatus.create(user.getId(), channel.getId());
  }

  /**
   * ReadStatusCreateRequest DTO 기반으로 ReadStatus 생성
   */
  public static ReadStatus createFromRequest(ReadStatusCreateRequest dto) {
    return ReadStatus.create(dto.userId(), dto.channelId());
  }
}
