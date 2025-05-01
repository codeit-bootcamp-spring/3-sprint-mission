package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.common.exception.ChannelException;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

  /**
   * 새로운 채널을 생성한다
   *
   * @param creatorId   채널 생성자 ID
   * @param name        채널명
   * @param description 채널 설명
   * @return 생성된 채널 객체
   */
  Channel createChannel(UUID creatorId, String name, String description);

  /**
   * 새로운 공개 채널 생성한다
   *
   * @param request ChannelCreateRequest
   * @return 생성된 채널 객체
   */
  Channel createPublicChannel(PublicChannelCreateRequest request);

  /**
   * 새로운 비공개 채널을 생성한다
   *
   * @param request ChannelCreateRequest
   * @return 생성된 채널 객체
   */
  Channel createPrivateChannel(PrivateChannelCreateRequest request);

  /**
   * ID로 채널을 조회한다
   *
   * @param id 채널 ID
   * @return 조회된 채널 객체
   */
  Optional<ChannelResponse> getChannelById(UUID id);

  /**
   * 채널을 검색한다
   *
   * @param creatorId 생성자 ID (null인 경우 모든 생성자)
   * @param name      채널명 (null인 경우 모든 이름)
   * @return 검색된 채널 목록
   */
  List<ChannelResponse> searchChannels(UUID creatorId, String name);

  /**
   * 사용자가 참여 중인 모든 채널을 조회한다
   *
   * @param userId 사용자 ID
   * @return 사용자가 참여 중인 채널 목록
   */
  List<ChannelResponse> getAllByUserId(UUID userId);

  /**
   * 채널 정보를 업데이트한다
   *
   * @param request ChannelUpdateRequest
   * @return 업데이트된 채널 객체
   */
  Optional<ChannelResponse> updateChannel(PublicChannelUpdateRequest request);

  /**
   * 채널에 참여자를 추가한다
   *
   * @param channelId 채널 ID
   * @param userId    추가할 사용자 ID
   * @throws ChannelException
   */
  void addParticipant(UUID channelId, UUID userId) throws ChannelException;

  /**
   * 채널에서 참여자를 제거한다
   *
   * @param channelId 채널 ID
   * @param userId    제거할 사용자 ID
   * @throws ChannelException
   */
  void removeParticipant(UUID channelId, UUID userId) throws ChannelException;

  /**
   * 채널을 삭제한다
   *
   * @param channelId 삭제할 채널 ID
   * @return 삭제된 채널 객체
   */
  Optional<ChannelResponse> deleteChannel(UUID channelId);
}
