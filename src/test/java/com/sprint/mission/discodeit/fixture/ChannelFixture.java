package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;

public class ChannelFixture {

  public static final String DEFAULT_CHANNEL_NAME = "테스트 채널";
  public static final String DEFAULT_CHANNEL_DESCRIPTION = "테스트 채널쓰임다";

  /**
   * 기본 유효한 Public Channel 생성
   */
  public static Channel createPublic() {
    return Channel.createPublic(DEFAULT_CHANNEL_NAME,
        DEFAULT_CHANNEL_DESCRIPTION);
  }

  /**
   * PublicChannelCreateRequest DTO 기반으로 Public Channel 생성
   */
  public static Channel createPublic(PublicChannelCreateRequest dto) {
    return Channel.createPublic(dto.name(), dto.description());
  }

  /**
   * 기본 유효한 Private Channel 생성
   */
  public static Channel createPrivate() {
    return Channel.createPrivate();
  }

  /**
   * PrivateChannelCreateRequest DTO 기반으로 Private Channel 생성
   */
  public static Channel createPrivate(PrivateChannelCreateRequest dto) {
    return Channel.createPrivate();
  }
}