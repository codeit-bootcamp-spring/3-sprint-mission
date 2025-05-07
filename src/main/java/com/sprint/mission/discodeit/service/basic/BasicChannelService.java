package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.dto.ChannelDTO;
import com.sprint.mission.discodeit.entity.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.entity.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.entity.dto.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.exception.CannotUpdateException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    ReadStatusRepository readStatusRepository;
    MessageRepository messageRepository;

    @Override
    public Channel create(CreatePrivateChannelRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        List<User> userList = request.userList(); // userList를 넣는게 중요해짐
        for (User user : userList) { // 일단 userList의 값들이 User 객체인지 유효성 검사는 안했음
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        }

        return channel;
    }

    @Override
    public Channel create(CreatePublicChannelRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());

        return channelRepository.save(channel);
    }

    // TODO
    //  - DTO를 활용하여:
    //    - [ ]  해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
    @Override
    public ChannelDTO find(UUID channelId) { // 애는 ChannelId임

        // 1. 채널 조회
        Channel channel = this.channelRepository
                .findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with channelId " + channelId + " not found"));

        // 2. 가장 최근 메시지 읽은 시간 조회
        Instant lastReadAt = readStatusRepository
                .findById(channelId)
                .getLastReadAt();

        // 3. PRIVATE 채널일 경우, 참여한 사용자 ID 정보 포함
        List<UUID> userIds = new ArrayList<>();

        // FIXME : userIds는 List<UUID>를 가져야함
        if (channel.getType() == ChannelType.PRIVATE) {
            // 3-1. ReadStatus에서 UserId를 여러 개 가져옴
            List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(channelId);

            // 참여한 모든 사용자 ID를 userIds에 추가
            for (ReadStatus readStatus : readStatuses) {
                userIds.add(readStatus.getUserId()); // 여러 개의 userId 추가
            }
            Optional<List<UUID>> optionalUserIds = Optional.ofNullable(userIds);
            ChannelDTO channelDTO = new ChannelDTO(channel, lastReadAt, optionalUserIds);

            return channelDTO;
        } else { // PUBLIC
            ChannelDTO channelDTO = new ChannelDTO(channel, lastReadAt, Optional.empty());

            return channelDTO;
        }

    }

    // FIXME : List<ChannelDTO>를 return하도록 수정
    @Override
    public List<ChannelDTO> findAllByUserId(UUID userId) {

        // 1. return 기본값 설정
        List<ChannelDTO> privateChannelDTOList = new ArrayList<>();
        List<ChannelDTO> publicChannelDTOList = new ArrayList<>();

        // 2. userId를 통해 channelId에 해당하는 DTO를 찾는다.
        List<ReadStatus> readStatuses = readStatusRepository.findByUserId(userId);

        // 3. 각 Channel에 대해 처리
        for (ReadStatus readStatus : readStatuses) {
            UUID channelId = readStatus.getChannelId();
            ChannelDTO channelDTO = this.find(channelId);
            // 4. 채널 타입이 PRIVATE인 경우
            if (channelDTO.channel().getType() == ChannelType.PRIVATE) {
                // PRIVATE 채널에 해당하는 userId를 포함시켜서 목록에 추가
                List<UUID> userIds = new ArrayList<>();
                userIds.add(userId); // 현재 userId를 userIds 리스트에 추가

                // PRIVATE 채널일 경우 User ID를 포함하여 ChannelDTO 생성
                ChannelDTO privateChannelDTO = new ChannelDTO(channelDTO.channel(), channelDTO.lastReadAt(), Optional.of(userIds));
                privateChannelDTOList.add(privateChannelDTO);

            } else { // PUBLIC 채널인 경우
                publicChannelDTOList.add(channelDTO);
            }
        }
        // 5. 최종적으로 PUBLIC과 PRIVATE 채널 리스트를 합쳐서 반환
        privateChannelDTOList.addAll(publicChannelDTOList);
        return privateChannelDTOList; // PRIVATE 채널 목록을 먼저 반환 (혹은 publicChannelDTOList를 반환할 수도 있음)
    }

    @Override
    public Channel update(UpdateChannelRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));

        if (channel.getType() == ChannelType.PUBLIC) {
            channel.update(request.newName(), request.newDescription());

            return channelRepository.save(channel);
        } else {
            throw new CannotUpdateException("PRIVATE 채널은 수정할 수 없습니다.");
        }
    }

    @Override
    public void delete(UUID channelId) {
        // 1. Channel 도메인 삭제
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        channelRepository.deleteById(channelId);

        // 2. ReadStatus 도메인 삭제
        List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(channelId);
        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.delete(readStatus.getUserId());
        }

        // 3. Message 도메인 삭제
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        for (Message message : messages) {
            messageRepository.deleteById(message.getId());
        }
    }
}
