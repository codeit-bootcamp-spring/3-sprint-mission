package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateNameRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    // 공개 채널
    Channel createPublicChannel(PublicChannelRequest request);

    // 비공개 채널
    Channel createPrivateChannel(PrivateChannelRequest request);

    // 채널에 유저 추가
    boolean addUserToChannel(UUID channelId, UUID userId, String password);

    // 채널 공개 여부 별로 조건 달아준 전체 조회
    List<ChannelFindResponse> findAllChannel(UUID userId);

    // 채널 이름으로 조회
    Optional<Channel> getChannelUsingName(String channelName);

    // 채널 아이디로 조회
    ChannelFindResponse getChannelUsingId(UUID channelId);

    // 채널 이름 수정
    boolean updateChannelName(ChannelUpdateNameRequest request);

//    //채널 공개 유무 수정
//    boolean updateChannelPrivateState(UUID channelId, UUID userId, String password, boolean isLock);

    //채널 삭제
    boolean deleteChannel(UUID channelId, UUID userId, String password);

    //채널 삭제
    boolean deleteUserInChannel(UUID channelId, UUID adminId, UUID userId, String password);
}
