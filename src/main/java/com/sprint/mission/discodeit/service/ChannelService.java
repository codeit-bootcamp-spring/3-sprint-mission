package com.sprint.mission.discodeit.service;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  public ChannelDto create(PublicChannelCreateRequest publicChannelCreateRequest);

  public ChannelDto create(PrivateChannelCreateRequest privateCreateRequest);

  public ChannelDto find(UUID channelId);

  public List<ChannelDto> findAllByUserId(UUID userId);

  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest updateRequest);

  public void delete(UUID channelId);

}
