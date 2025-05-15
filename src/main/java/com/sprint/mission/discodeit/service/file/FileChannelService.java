package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class FileChannelService implements ChannelService {

    private final UserService userService;
    private final ChannelRepository channelRepository;
    // Channel 참가자 관리를 위한 임시 Set. 실제 Channel 엔티티에 필드가 추가되어야 함.
    private final Set<UUID> participants = new HashSet<>();

    public FileChannelService(UserService userService, ChannelRepository channelRepository) {
        this.userService = userService;
        this.channelRepository = channelRepository;
    }

    /**
     * 새 공개 채널 생성
     *
     * @param request 공개 채널 생성 요청 DTO
     */
    @Override
    public Channel createChannel(PublicChannelCreateRequest request) {
        // UserDto를 반환하는 userService.getUserById 사용
        if (userService.getUserById(request.ownerChannelId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // Channel 생성자 및 ChannelType 사용, DTO 필드 접근 수정
        Channel channel = new Channel(ChannelType.PUBLIC, request.channelName(), request.password(), request.ownerChannelId());
        // 참가자 로직은 Channel 엔티티에 필드 추가 후 수정 필요
        // channel.addParticipant(request.ownerId()); // Channel 엔티티에 없음
        return channelRepository.save(channel);
    }

    /**
     * 새 비공개 채널 생성
     *
     * @param request 비공개 채널 생성 요청 DTO
     */
    @Override
    public Channel createChannel(PrivateChannelCreateRequest request) {
        // PrivateChannelCreateRequest는 participantIds만 가짐. 현재 DTO 구조로는 채널 생성에 필요한 정보 부족.
        // 이 메소드의 목적이 채널 생성이 맞다면 DTO 수정 필요. 현재는 컴파일 오류 해결을 위해 최소한의 형태로 유지.

        // 현재 DTO만으로는 ownerId, channelName, password를 알 수 없음.
        // 임시로 하드코딩하거나 로직 변경 필요.
        // 여기서는 컴파일 오류만 해결하고 기능 구현은 추후에.

        // 예시: DTO에 필요한 필드가 추가된다면:
        // if (userService.getUserById(request.ownerId()) == null) {
        //     throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        // }
        // Channel channel = new Channel(ChannelType.PRIVATE, request.channelName(), request.password(), request.ownerId());
        // request.participantIds().forEach(channel::addParticipant); // Channel 엔티티에 addParticipant 필요
        // return channelRepository.save(channel);

        // 현재 DTO 구조에서는 채널 생성을 제대로 구현하기 어려우므로 임시 처리 또는 예외 발생
        throw new UnsupportedOperationException("Private channel creation with current DTO structure is not fully implemented.");
        // return null; // 혹은 null 반환 (좋은 방법은 아님)
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        // Optional 처리
        Optional<Channel> channelOptional = channelRepository.findById(channelId);
        return channelOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    /**
     * 특정 채널 정보 업데이트
     *
     * @param request 채널 업데이트 요청 DTO
     */
    @Override
    public Channel updateChannel(PublicChannelUpdateRequest request) {
        // Optional 처리
        Optional<Channel> channelOptional = channelRepository.findById(request.channelId());
        Channel channel = channelOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));

        // 업데이트 로직 (Channel 엔티티에 update 메소드 필요)
        if (request.channelName() != null && !request.channelName().isEmpty()) {
            channel.updateChannelName(request.channelName()); // Channel 엔티티에 updateChannelName 있음
        }
        if (request.password() != null && !request.password().isEmpty()) {
             channel.updatePassword(request.password()); // Channel 엔티티에 updatePassword 있음
        }
        // 비공개 여부 업데이트 로직은 PublicChannelUpdateRequest에 isPrivate 필드가 있다면 추가
        // if (request.isPrivate() != channel.isPrivate()) { channel.updatePrivate(request.isPrivate()); } // Channel 엔티티에 isPrivate, updatePrivate 필요

        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.deleteById(channelId);
    }

    // ChannelService 인터페이스에 정의되지 않은 메소드들은 주석 처리

//    public Set<UUID> getChannelParticipants(UUID channelId) {
//        // 로직 구현 (Channel 엔티티에 참가자 목록 필드 필요)
//        throw new UnsupportedOperationException("getChannelParticipants not implemented.");
//    }

//    public boolean joinChannel(UUID channelId, UUID userId, String password) {
//        // 로직 구현 (Channel 엔티티에 참가자 관리 메소드 필요)
//        throw new UnsupportedOperationException("joinChannel not implemented.");
//    }

//    public boolean leaveChannel(UUID channelId, UUID userId) {
//        // 로직 구현 (Channel 엔티티에 참가자 관리 메소드 필요)
//        throw new UnsupportedOperationException("leaveChannel not implemented.");
//    }
}
