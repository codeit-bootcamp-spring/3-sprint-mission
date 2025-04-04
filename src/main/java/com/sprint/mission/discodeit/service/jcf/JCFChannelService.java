package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    // 데이터를 저장하는 필드 (ID를 키로 사용)
    private final Map<UUID, Channel> data;

    // 생성자: HashMap을 사용해 초기화
    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel createChannel(String ChannelName, boolean isPrivate, String password,UUID ownerChannelId) {
        Channel channel = new Channel(ChannelName, isPrivate, password, ownerChannelId); // 새로운 채널 생성
        data.put(channel.getChannelId(), channel); // 데이터 저장
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

        // 이미 참가한 사용자인지 확인
        if (channel.isParticipant(userId)) {
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