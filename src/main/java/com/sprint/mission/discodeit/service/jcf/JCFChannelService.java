package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFChannelService implements ChannelService {

    // 데이터를 저장하는 필드 (ID를 키로 사용)
    private final Map<UUID, Channel> data;
    private final UserService userService;

    // 생성자: UserService 주입
    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String channelName, boolean isPrivate, String password, UUID ownerChannelId) {
        // 소유자 존재 여부 확인
        if (userService.getUserById(ownerChannelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID로는 채널을 생성할 수 없습니다.");
        }
        Channel channel = new Channel(channelName, isPrivate, password, ownerChannelId);
        // 채널 생성 시 소유자를 자동으로 참가자로 추가
        channel.addParticipant(ownerChannelId);
        data.put(channel.getChannelId(), channel);
        return channel;
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        return data.get(channelId); // ID로 채널 조회
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values()); // 모든 채널 목록 반환
    }

    @Override
    public void updateChannel(UUID channelId, String ChannelName, boolean isPrivate, String password) {
        Channel channel = data.get(channelId);
        if (channel != null) {
            if (ChannelName != null && !ChannelName.isEmpty()) {
                channel.updateChannelName(ChannelName); // 채널명 업데이트
            }
            channel.updatePrivate(isPrivate);  // 공개 여부 업데이트
            if (password != null && !password.isEmpty()) {
                channel.updatePassword(password); // 비밀번호 업데이트
            }
        }
    }

    @Override
    public boolean joinChannel(UUID channelId, UUID userId, String password) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
        // 참가 사용자 존재 여부 확인
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID로는 채널에 참가할 수 없습니다.");
        }

        // 이미 참가한 사용자인지 확인
        if (channel.isParticipant(userId)) {
            // 이미 참가했으므로 false 반환 또는 예외 처리 (요구사항에 따라)
            // 여기서는 false를 반환하여 변화가 없음을 알림
            return false;
        }

        // 비공개 채널인 경우 비밀번호 확인
        if (channel.isPrivate() && !channel.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return channel.addParticipant(userId);
    }

    @Override
    public boolean leaveChannel(UUID channelId, UUID userId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }

        // 채널 소유자는 나갈 수 없음
        if (channel.getOwnerChannelId().equals(userId)) {
            throw new IllegalArgumentException("채널 소유자는 채널을 나갈 수 없습니다.");
        }

        return channel.removeParticipant(userId);
    }

    @Override
    public Set<UUID> getChannelParticipants(UUID channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
        return channel.getParticipants();
    }

    @Override
    public void deleteChannel(UUID channelId) {
        data.remove(channelId); // 채널 데이터 삭제
    }
}
