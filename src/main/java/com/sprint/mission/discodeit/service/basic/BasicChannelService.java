package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateNameRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    //레포지토리 의존성
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private static final Logger logger = Logger.getLogger(BasicChannelService.class.getName()); // 필드로 Logger 선언

    // 공개 채널 생성
    @Override
    public Channel createPublicChannel(PublicChannelRequest request) {

        System.out.println("공개 채널 생성 메서드 들어옴");

        Channel channel = new Channel(
                request.channelName(),
                request.adminId(),
                ChannelType.PUBLIC,
                ""
        );

        if (channelRepository.findChannelUsingName(request.channelName()).isPresent()) {
            throw new IllegalArgumentException("채널 이름이 이미 존재합니다.");
        }

        System.out.println("채널 생성됨");

        channel.getJoiningUsers().add(channel.getAdminId());
        channelRepository.saveChannel(channel);

        return channel;
    }

    // 비공개 채널 생성
    @Override
    public Channel createPrivateChannel(PrivateChannelRequest request) {

        Channel channel = new Channel(
                request.channelName(),
                request.adminId(),
                ChannelType.PRIVATE,
                request.password()
        );

        if (channelRepository.findChannelUsingName(request.channelName()).isPresent()) {
            throw new IllegalArgumentException("채널 이름이 이미 존재합니다.");
        }

        channel.getJoiningUsers().add(channel.getAdminId());
        channelRepository.saveChannel(channel);


        // 채널 참여자 리스트에 추가
        for (UUID participantId : request.participantIds()) {
            channel.getJoiningUsers().add(participantId);
            channelRepository.saveChannel(channel);
        }

        // 채널 참여자 별 readStatus 생성하기
        for (UUID participantId : channel.getJoiningUsers()) {
            ReadStatus readStatus = new ReadStatus(participantId, channel.getId(), Instant.now());
            readStatusRepository.saveReadStatus(readStatus);
        }

        return channel;
    }

    // 채널 공개 여부 별로 조건 달아준 전체 조회
    public List<ChannelFindResponse> findAllChannel(UUID userId) {

        List<Channel> channels = channelRepository.findAllChannels();

        return channels.stream()
                .filter(channel -> {
                    if (channel.getLock() == ChannelType.PUBLIC) {
                        return true;
                    } else {
                        List<UUID> participantIds = channel.getJoiningUsers();
                        return participantIds.contains(userId);
                    }
                })
                .map(channel -> {
                    // 최신 메시지 시간 조회
                    List<Message> findMessageByChannelId = messageRepository.findMessageByChannel(channel.getId());
                    Instant latestMessageTime = findMessageByChannelId.stream()
                            .map(Message::getCreatedAt)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    if (latestMessageTime == null) {
                        logger.warning("ChannelService: 메시지가 없습니다.");
                    }

                    List<UUID> participantIds = channel.getJoiningUsers();

                    return new ChannelFindResponse(
                            channel.getId(),
                            channel.getChannelName(),
                            latestMessageTime,
                            participantIds
                    );
                })
                .toList();
    }

    // 이름만으로 채널 조회
    @Override
    public Optional<Channel> getChannelUsingName(String channelName) {

        return channelRepository.findChannelUsingName(channelName);
    }

    // id로 채널 조회
    @Override
    public ChannelFindResponse getChannelUsingId(UUID channelId) {

        Channel channel = channelRepository.findChannelUsingId(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        List<Message> findMessageByChannelId = messageRepository.findMessageByChannel(channelId);
        Instant latestMessageTime = findMessageByChannelId.stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);


        if (channel.getLock() == ChannelType.PUBLIC) {
            return new ChannelFindResponse(
                    channel.getId(),
                    channel.getChannelName(),
                    latestMessageTime,
                    null
            );
        }
        else {
            return new ChannelFindResponse(
                    channel.getId(),
                    channel.getChannelName(),
                    latestMessageTime,
                    channel.getJoiningUsers()
            );
        }
    }

    // 채널 이름 수정
    @Override
    public boolean updateChannelName(ChannelUpdateNameRequest request) {

        Channel channel = channelRepository.findChannelUsingId(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        if (channel.getLock() != ChannelType.PUBLIC) {
            throw new IllegalArgumentException("비공개 채널은 이름을 수정할 수 없습니다.");
        }

        channel.updateChannelName(request.newName());
        channel.updateUpdatedAt(Instant.now());
        logger.info("ChannelService: 채널 이름 수정이 완료되었습니다.");

        return true;
    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId, UUID userId, String password) {
        Optional<Channel> foundChannel = channelRepository.findChannelUsingId(channelId);

        if(foundChannel.isPresent()){
            Channel channel = foundChannel.get();
            if ((isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

                List<Message> messages = messageRepository.findMessageByChannel(channelId);
                for (Message message : messages) {
                    messageRepository.deletedMessage(message.getMessageId());
                }
                logger.info("ChannelService: 해당 채널의 메시지가 삭제되었습니다.");

                List<ReadStatus> readStatuses = readStatusRepository.findReadStatusByChannelId(channelId);
                for (ReadStatus readStatus : readStatuses) {
                    readStatusRepository.deleteReadStatusById(readStatus.getId());
                }
                logger.info("ChannelService: 해당 채널의 ReadStatus가 삭제되었습니다.");

                channelRepository.deleteChannel(channelId);
                logger.info("ChannelService: 채널이 삭제되었습니다.");

                return true;
            }
            return false;
        }
        logger.warning("ChannelService: 채널이 존재하지 않습니다.");
        return false;
    }

    // 채널에 유저 추가
    @Override
    public boolean addUserToChannel(UUID channelId, UUID userId, String password) {
        Optional<Channel> foundChannel = channelRepository.findChannelUsingId(channelId);
        if(foundChannel.isPresent()){
            Channel channel = foundChannel.get();

            if(isChannelLock(channel, password)) {

                channel.getJoiningUsers().add(userId);
                channel.updateUpdatedAt(Instant.now());
                logger.info(channel.getChannelName() + "채널에" + userId + " 유저가 추가되었습니다");
                channelRepository.saveChannel(channel);

                ReadStatus readStatus = new ReadStatus(userId, channelId, Instant.now());
                readStatusRepository.saveReadStatus(readStatus);

                return true;
            }
            return false;
        }
        logger.warning("ChannelService: 채널이 존재하지 않습니다.");
        return false;
    }

    // 참여한 유저 삭제
    @Override
    public boolean deleteUserInChannel(UUID channelId, UUID adminId, UUID userId, String password) {
        Optional<Channel> foundChannel = channelRepository.findChannelUsingId(channelId);
        if(foundChannel.isPresent()) {
            Channel channel = foundChannel.get();

            if((isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

                List<ReadStatus> readStatusByChannel = readStatusRepository.findReadStatusByChannelId(channelId);
                Optional<ReadStatus> readStatuses =  readStatusByChannel.stream()
                        .filter(readStatus -> readStatus.getUserId().equals(userId))
                        .findFirst();

                readStatuses.ifPresent(readStatus -> readStatusRepository.deleteReadStatusById(readStatus.getId()));


                channel.getJoiningUsers().remove(userId);
                channel.updateUpdatedAt(Instant.now());
                channelRepository.saveChannel(channel);

                logger.info("채널의 " + userId + " 유저가 삭제되었습니다");
                return true;
            }
            return false;
        }
        logger.warning("ChannelService: 채널이 존재하지 않습니다.");
        return false;
    }

    // 채널 유효성 검사 - 존재여부
    private boolean isChannelExist(Channel channel){
        if(channel == null){
            logger.warning("채널이 존재하지 않습니다.");
            return false;
        }
        return true;
    }
    // 채널 유효성 검사 - 관리자 대조
    private boolean isChannelAdmin(Channel channel, UUID userId){
        if(!channel.getAdminId().equals(userId)){
            logger.warning("채널 정보 수정 권한이 없습니다.");
            return false;
        }
        return true;
    }
    // 채널 유효성 검사 - 비밀번호 대조
    private boolean isChannelLock(Channel channel, String password){
        if(channel.getLock() == ChannelType.PRIVATE){
            logger.info("비공개 채널입니다. 비밀번호를 확인중입니다.");
            if(!channel.getPassword().equals(password)){
                logger.warning("비밀번호가 일치하지 않습니다.");
                return false;
            }
        }
        return true;
    }
}
