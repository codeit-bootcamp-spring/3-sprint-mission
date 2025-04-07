package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Java Collection Framework를 사용하여 채널 데이터를 메모리에 저장하고 관리하는 서비스 구현체.
 * 사용자 존재 여부 검증을 위해 UserService에 의존합니다.
 */
public class JCFChannelService implements ChannelService {
    // 데이터를 저장하는 필드 (ID를 키로 사용)
    private final Map<UUID, Channel> data;
    private final UserService userService;

    /**
     * JCFChannelService 생성자.
     * UserService를 주입받아 채널 생성 및 참여 시 사용자 존재 여부를 검증하는 데 사용합니다.
     *
     * @param userService 사용자 관련 로직을 처리하는 서비스
     */
    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    /**
     * 새로운 채널을 생성합니다.
     * 채널 소유자 ID로 사용자가 존재하는지 검증한 후 채널을 생성하고 저장합니다.
     *
     * @param channelName    생성할 채널의 이름
     * @param isPrivate      채널의 공개 여부
     * @param password       비공개 채널일 경우 사용할 비밀번호 (공개 시 null 또는 빈 문자열)
     * @param ownerChannelId 채널 소유자의 UUID
     * @return 생성된 채널 객체
     * @throws IllegalArgumentException 채널 소유자 ID에 해당하는 사용자가 존재하지 않을 경우 발생
     */
    @Override
    public Channel createChannel(String channelName, boolean isPrivate, String password, UUID ownerChannelId) {
        // 채널 소유자 존재 여부 검증
        if (userService.getUserById(ownerChannelId) == null) {
            throw new IllegalArgumentException("채널 소유자(User)가 존재하지 않습니다. ID: " + ownerChannelId);
        }

        Channel channel = new Channel(channelName, isPrivate, password, ownerChannelId);
        data.put(channel.getChannelId(), channel);
        return channel;
    }

    /**
     * 주어진 UUID에 해당하는 채널 객체를 반환합니다.
     *
     * @param channelId 조회할 채널의 UUID
     * @return 해당 UUID를 가진 채널 객체. 존재하지 않으면 null 반환.
     */
    @Override
    public Channel getChannelById(UUID channelId) {
        return data.get(channelId); // ID로 채널 조회
    }

    /**
     * 현재 저장된 모든 채널의 목록을 반환합니다.
     *
     * @return 모든 채널 객체를 담은 List. 채널이 없으면 빈 List 반환.
     */
    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values()); // 모든 채널 목록 반환
    }

    /**
     * 특정 채널의 정보를 업데이트합니다.
     * 채널 이름, 공개 여부를 업데이트할 수 있습니다.
     * 비공개 채널인 경우에만 비밀번호를 업데이트합니다.
     * null 또는 빈 문자열이 아닌 값만 업데이트됩니다.
     *
     * @param channelId   업데이트할 채널의 UUID
     * @param channelName 새로운 채널 이름 (변경 원치 않으면 null 또는 기존 이름)
     * @param isPrivate   새로운 공개 여부 설정
     * @param password    새로운 비밀번호 (변경 원치 않거나 공개 채널이면 null 또는 빈 문자열)
     */
    @Override
    public void updateChannel(UUID channelId, String channelName, boolean isPrivate, String password) {
        Channel channel = data.get(channelId);
        if (channel != null) {
            if (channelName != null && !channelName.isEmpty()) {
                channel.updateChannelName(channelName); // 채널명 업데이트
            }
            channel.updatePrivate(isPrivate);  // 공개 여부 업데이트

            // 비공개 채널일 경우에만 비밀번호 업데이트
            if (channel.isPrivate()) {
                if (password != null && !password.isEmpty()) {
                    channel.updatePassword(password);
                }
            } 
        }
    }

    /**
     * 사용자가 특정 채널에 참여합니다.
     * 채널 및 사용자 존재 여부를 검증하고, 비공개 채널의 경우 비밀번호를 확인합니다.
     * 이미 참여 중인 사용자는 추가되지 않습니다.
     *
     * @param channelId 참여할 채널의 UUID
     * @param userId    참여할 사용자의 UUID
     * @param password  비공개 채널 참여 시 필요한 비밀번호
     * @return 성공적으로 참여하면 true, 이미 참여 중이면 false 반환
     * @throws IllegalArgumentException 채널 또는 사용자가 존재하지 않거나, 비공개 채널의 비밀번호가 틀릴 경우 발생
     */
    @Override
    public boolean joinChannel(UUID channelId, UUID userId, String password) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }

        // 사용자 존재 여부 검증
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("참여하려는 사용자(User)가 존재하지 않습니다. ID: " + userId);
        }

        // 이미 참가한 사용자인지 확인
        if (channel.isParticipant(userId)) {
            return false; // 이미 참여 중
        }

        // 비공개 채널 비밀번호 확인
        if (channel.isPrivate()) {
            if (password == null || !channel.getPassword().equals(password)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        }

        return channel.addParticipant(userId);
    }

    /**
     * 사용자가 특정 채널에서 나갑니다.
     * 채널 소유자는 채널을 나갈 수 없으며, 대신 deleteChannel 메서드를 사용해 채널을 삭제할 수 있습니다.
     *
     * @param channelId 나갈 채널의 UUID
     * @param userId    나갈 사용자의 UUID
     * @return 성공적으로 나가면 true, 사용자가 없었으면 false 반환
     * @throws IllegalArgumentException 채널이 존재하지 않거나, 사용자가 채널 소유자일 경우 발생
     */
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

    /**
     * 특정 채널의 참여자 목록(UUID Set)을 반환합니다.
     *
     * @param channelId 참여자 목록을 조회할 채널의 UUID
     * @return 해당 채널의 참여자 UUID Set. 방어적 복사를 통해 반환됨.
     * @throws IllegalArgumentException 채널이 존재하지 않을 경우 발생
     */
    @Override
    public Set<UUID> getChannelParticipants(UUID channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
        return channel.getParticipants(); // Channel 엔티티에서 복사본을 반환함
    }

    /**
     * 특정 채널을 삭제합니다.
     *
     * @param channelId 삭제할 채널의 UUID
     */
    @Override
    public void deleteChannel(UUID channelId) {
        data.remove(channelId); // 채널 데이터 삭제
    }
}