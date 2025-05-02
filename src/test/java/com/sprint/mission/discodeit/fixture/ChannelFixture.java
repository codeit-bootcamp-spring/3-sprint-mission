package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public class ChannelFixture {

  public static final String DEFAULT_CHANNEL_NAME = "테스트 채널";
  public static final String DEFAULT_CHANNEL_DESCRIPTION = "테스트 채널쓰임다";

  /**
   * 기본 채널 생성 (Public) + 생성자를 참여자로 추가
   */
  public static Channel createValidChannelWithParticipant() {
    User creator = UserFixture.createValidUser();
    Channel channel = Channel.create(creator.getId(), DEFAULT_CHANNEL_NAME,
        DEFAULT_CHANNEL_DESCRIPTION);
    channel.addParticipant(creator.getId());
    return channel;
  }

  /**
   * 사용자 정의 Public 채널 생성 + 생성자를 참여자로 추가
   */
  public static Channel createCustomChannelWithParticipant(User creator, String name,
      String description) {
    Channel channel = Channel.create(creator.getId(), name, description);
    channel.addParticipant(creator.getId());
    return channel;
  }

  /**
   * Public 채널 생성 (Request 기반) + 생성자를 참여자로 추가
   */
  public static Channel createCustomPublicChannelWithParticipant(
      PublicChannelCreateRequest request) {
    Channel channel = Channel.createPublic(request.creator(), request.name(),
        request.description());
    channel.addParticipant(request.creator());
    return channel;
  }

  /**
   * Private 채널 생성 (Request 기반) + 생성자를 참여자로 추가
   */
  public static Channel createCustomPrivateChannelWithParticipant(
      PrivateChannelCreateRequest request) {
    Channel channel = Channel.createPrivate(request.creator());
    channel.addParticipant(request.creator());
    return channel;
  }

  /**
   * 기본 채널 생성 (Public) - 참여자 미포함
   */
  public static Channel createValidChannel() {
    User creator = UserFixture.createValidUser();
    return Channel.create(creator.getId(), DEFAULT_CHANNEL_NAME, DEFAULT_CHANNEL_DESCRIPTION);
  }

  /**
   * 사용자 정의 Public 채널 생성 - 참여자 미포함
   */
  public static Channel createCustomChannel(User creator, String name, String description) {
    return Channel.create(creator.getId(), name, description);
  }

  /**
   * Public 채널 생성 (Request 기반) - 참여자 미포함
   */
  public static Channel createCustomPublicChannel(PublicChannelCreateRequest request) {
    return Channel.createPublic(request.creator(), request.name(), request.description());
  }

  /**
   * Private 채널 생성 (Request 기반) - 참여자 미포함
   */
  public static Channel createCustomPrivateChannel(PrivateChannelCreateRequest request) {
    return Channel.createPrivate(request.creator());
  }
}