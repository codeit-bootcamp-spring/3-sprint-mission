package com.sprint.mission.discodeit.service;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    public ChannelCreateResponse create(PublicChannelCreateRequest publicChannelCreateRequest);

    public ChannelCreateResponse create(PrivateChannelCreateRequest privateCreateRequest);

    public ChannelResponse find(UUID channelId);

    public List<ChannelResponse> findAllByUserId(UUID userId);

    public ChannelCreateResponse update(ChannelUpdateRequest updateRequest);

    public void delete(UUID channelId);

    public void addMessageToChannel(UUID channelId, UUID messageId);

    // == joinChannel()
    public void addAttendeeToChannel(UUID channelId, UUID userId);

    // == leaveChannel()
    public void removeAttendeeToChannel(UUID channelId, UUID userId);

    public List<User> findAttendeesByChannel(UUID channelId);


}
