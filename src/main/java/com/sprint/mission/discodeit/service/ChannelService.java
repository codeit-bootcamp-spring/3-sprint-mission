package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

// 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
public interface ChannelService {

  // 생성 ( PRIVATE & PUBLIC )
  ChannelDto create(PrivateChannelCreateRequest request);

  ChannelDto create(PublicChannelCreateRequest request);

  // 특정 채널 조회
  ChannelDto find(UUID channelId);

  // 유저가 확인 가능한 채널 목록( PRIVATE CHANNEL 포함 )
  List<ChannelDto> findAllByUserId(UUID userId);

  // 특정 채널 정보 수정
  ChannelDto update(UUID channelId, PublicChannelUpdateRequest request);

  // 특정 채널 제거
  void delete(UUID channelId);
}
