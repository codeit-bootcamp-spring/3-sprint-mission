package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

/**
 * 채널 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface ChannelService {

  /**
   * 공개 채널을 생성합니다.
   * 
   * @param request 공개 채널 생성 요청 정보
   * @return 생성된 채널 엔티티
   */
  Channel create(PublicChannelCreateRequest request);

  /**
   * 비공개 채널을 생성합니다.
   * 참가자들의 ReadStatus도 함께 생성됩니다.
   * 
   * @param request 비공개 채널 생성 요청 정보 (참가자 ID 목록 포함)
   * @return 생성된 채널 엔티티
   */
  Channel create(PrivateChannelCreateRequest request);

  /**
   * 채널 ID로 채널 정보를 조회합니다.
   * 
   * @param channelId 조회할 채널의 ID
   * @return 채널 DTO
   */
  ChannelDto find(UUID channelId);

  /**
   * 사용자가 접근 가능한 모든 채널 목록을 조회합니다.
   * 공개 채널과 사용자가 구독한 비공개 채널이 포함됩니다.
   * 
   * @param userId 사용자 ID
   * @return 접근 가능한 채널 DTO 목록
   */
  List<ChannelDto> findAllByUserId(UUID userId);

  /**
   * 공개 채널의 정보를 수정합니다.
   * 비공개 채널은 수정할 수 없습니다.
   * 
   * @param channelId 수정할 채널의 ID
   * @param request   수정할 채널 정보
   * @return 수정된 채널 엔티티
   */
  Channel update(UUID channelId, PublicChannelUpdateRequest request);

  /**
   * 채널을 삭제합니다.
   * 연관된 메시지와 ReadStatus도 함께 삭제됩니다.
   * 
   * @param channelId 삭제할 채널의 ID
   */
  void delete(UUID channelId);
}