package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class FileChannelService implements ChannelService {

    private final UserService userService;
    private final ChannelRepository channelRepository;

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
        if (userService.getUserById(request.ownerId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // Channel 생성자 및 ChannelType 사용, DTO 필드 접근 수정
        Channel channel = new Channel(ChannelType.PUBLIC, request.channelName(), null, request.ownerId());
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
        // DTO로부터 채널 이름, 소유자 ID, 참가자 목록을 가져옴
        String channelName = request.channelName();
        UUID ownerId = request.ownerId();
        List<UUID> participantIds = request.participantIds();

        // 소유자 유효성 검사 (UserService가 주입되어 있음)
        if (userService.getUserById(ownerId) == null) { 
            throw new IllegalArgumentException("Invalid owner ID for private channel creation: " + ownerId);
        }

        // Channel 엔티티 생성 (비밀번호는 null로 설정)
        Channel channel = new Channel(ChannelType.PRIVATE, channelName, null, ownerId);
        // Channel 생성자에서 ownerId는 이미 participantIds에 추가됨
        // DTO로 받은 추가 참가자들을 채널에 추가
        if (participantIds != null) {
            for (UUID participantId : participantIds) {
                channel.addParticipant(participantId); // Channel 엔티티의 addParticipant 사용
            }
        }

        // File 기반 Repository에 저장
        return channelRepository.save(channel);
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

    @Override
    public List<ChannelDto> getChannelsByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll(); // Reads all channels from file(s)
        List<Channel> userChannels = allChannels.stream()
                .filter(channel -> channel.getParticipantIds() != null && channel.getParticipantIds().contains(userId))
                .collect(Collectors.toList());

        return userChannels.stream()
                .map(channel -> new ChannelDto(
                        channel.getChannelId(),
                        channel.getChannelType(),
                        channel.getChannelName(),
                        channel.getPassword(),
                        channel.getParticipantIds(),
                        channel.getLastMessageAt() // Assuming Channel entity as read from file has this
                ))
                .collect(Collectors.toList());
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
