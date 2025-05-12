package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

  /**
   * 새로운 공개 채널 생성한다
   *
   * @param request ChannelCreateRequest
   * @return 생성된 채널 객체
   */
  ChannelResponse create(PublicChannelCreateRequest request);

  /**
   * 새로운 비공개 채널을 생성한다
   *
   * @param request ChannelCreateRequest
   * @return 생성된 채널 객체
   */
  ChannelResponse create(PrivateChannelCreateRequest request);

  /**
   * ID로 채널을 조회한다
   *
   * @param id 채널 ID
   * @return 조회된 채널 객체
   */
  Optional<ChannelResponse> findById(UUID id);

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
   * @param request ChannelUpdateRequest
   * @return 업데이트된 채널 객체
   */
  Optional<ChannelResponse> update(PublicChannelUpdateRequest request);

  /**
   * 채널을 삭제한다
   *
   * @param channelId 삭제할 채널 ID
   * @return 삭제된 채널 객체
   */
  Optional<ChannelResponse> delete(UUID channelId);
}
