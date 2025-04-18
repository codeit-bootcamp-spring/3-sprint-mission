package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface ChatService {
    // 채널 입장
    public Channel enterChannel(String userId, String channelName);
    // 채널 퇴장
    public Channel leaveChannel(User user, Channel joinChannel);
    // 메시지 전송
    public Message sendMessage(Channel sendChannel, User sendUser, String msgContent);
    // 유저의 채널리스트에서 채널 삭제
    public Channel deleteChannelFromUsers(UUID id);
    // 채널의 메시지리스트에서 메시지 삭제
    public void deleteMessageFromChannel(UUID id);
}

