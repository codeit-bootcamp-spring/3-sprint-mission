package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  /**
   * 새로운 공개 채널 생성한다
   *
   * @param name        채널 이름
   * @param description 채널 설명
   * @return 생성된 채널 객체
   */
  ChannelResponse create(String name, String description);

  /**
   * 새로운 비공개 채널을 생성한다
   *
   * @param participantIds 채널 참여자 IDs
   * @return 생성된 채널 객체
   */
  ChannelResponse create(List<UUID> participantIds);

  /**
   * ID로 채널을 조회한다
   *
   * @param id 채널 ID
   * @return 조회된 채널 객체
   */
  ChannelResponse findById(UUID id);

  /**
   * 사용자가 참여 중인 모든 채널을 조회한다
   *
   * @param userId 사용자 ID
   * @return 사용자가 참여 중인 채널 목록
   */
  List<ChannelResponse> findAllByUserId(UUID userId);

  /**
   * 채널 정보를 업데이트한다
   *
   * @param channelId      UUID
   * @param newName        새로운 채널명
   * @param newDescription 새로운 채널 설명
   * @return 업데이트된 채널 객체
   */
  ChannelResponse update(
      UUID channelId,
      String newName,
      String newDescription);

  /**
   * 채널을 삭제한다
   *
   * @param channelId 삭제할 채널 ID
   * @return 삭제된 채널 객체
   */
  ChannelResponse delete(UUID channelId);
}
