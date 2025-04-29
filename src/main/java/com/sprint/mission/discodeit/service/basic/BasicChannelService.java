package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelCreateResponse create(PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = new Channel(publicChannelCreateRequest);

        //FIXME : ChannelCreateResponse에는 channel 밖에없는데
        this.channelRepository.save(channel);

        return new ChannelCreateResponse(channel);
    }

    /* User 별 ReadStatus 생성 , name과 description 생략 */
    //QUESTION. 채널이 생성될때 user는 owner밖에 없는데??
    public ChannelCreateResponse create(PrivateChannelCreateRequest privateCreateRequest) {
        Channel channel = new Channel(privateCreateRequest);
        ReadStatus readStatus = new ReadStatus(privateCreateRequest.ownerId(), channel.getId());

        //TODO : attendees 에 추가해야함

        this.channelRepository.save(channel);

        return new ChannelCreateResponse(channel);

    }

    @Override
    public ChannelResponse find(UUID channelId) {

        Channel channel = this.channelRepository
                .findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        //TODO : 에러처리
        List<UUID> attendees = channel.getType().equals(ChannelType.PRIVATE) ? channel.getAttendees() : null;

        // FIXME : 나중에 lastMessageTime 찾아서 넣어줘야함
        return new ChannelResponse(channel, Instant.now(), attendees);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {

        // get public channel
        List<Channel> publicChannels = this.channelRepository
                .findAll();

        // get private channel by userId
        List<Channel> privateChannels = this.channelRepository
                .findAll()
                .stream().filter((channel) -> {
                    return channel.getType().equals(ChannelType.PRIVATE) && channel.getAttendees().contains(userId);
                }).toList();

        List<ChannelResponse> channelResponses = Stream.concat(publicChannels.stream(), privateChannels.stream()).map((channel) -> {
            List<UUID> attendees = channel.getType().equals(ChannelType.PRIVATE) ? channel.getAttendees() : null;
            // FIXME : 나중에 lastMessageTime 찾아서 넣어줘야함
            return new ChannelResponse(channel, Instant.now(), attendees);
        }).toList();

        return channelResponses;
    }

    @Override
    public ChannelCreateResponse update(ChannelUpdateRequest updateRequest) {
        // 다른 메소드에서 this.find() 이제 사용 못함. response 타입이 UserResponse로 바뀌어서.
        Channel channel = this.channelRepository.findById(updateRequest.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + updateRequest.channelId() + " not found"));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new UnsupportedOperationException("private 채널은 수정이 불가능합니다.");
        }

        channel.update(updateRequest.newName(), updateRequest.newDescription());

        //QUESTION: 이렇게 하면 파일레포일때 파일 업데이트가되나???
        return new ChannelCreateResponse(channel);
    }

    @Override
    public void delete(UUID channelId) {

        Channel channel = this.channelRepository.findById(channelId).
                orElseThrow(() -> new NoSuchElementException("Channel with channelId " + channelId + " not found"));


        //TODO : Message에서 해당 채널과 관련된 객체 삭제
//        List<Message> messages = this.messageRepository.findAllByChannelId(channelId);
//        for(Message message : messages){
//            this.messageRepository.deleteById(message.getId());
//        }

        //TODO : ReadStatus에서 해당 채널과 관련된 객체 삭제
//        List<ReadStatus> channelStatuses = this.ReadStatusRepository.findAllByChannelId(channelId)
        //        for(ChannelStatus channelStatus : channelStatuses){
//                 this.ReadStatusRepository.deleteById(channelStatus.getId());
//        }

        this.channelRepository.deleteById(channelId);
    }

    @Override
    public void addMessageToChannel(UUID channelId, UUID messageId) {
//        Channel channel = this.find(channelId).channel();
//        channel.addMessage(messageId);
//        this.create(channel);
    }

    @Override
    public void addAttendeeToChannel(UUID channelId, UUID userId) {
//        Channel channel = this.find(channelId).channel();
//        channel.addAttendee(userId);
//        this.create(channel);
    }

    @Override
    public void removeAttendeeToChannel(UUID channelId, UUID userId) {
        Channel channel = this.find(channelId).channel();
        channel.removeAttendee(userId);
        //FIXME
//        this.create(channel);
    }

    @Override
    public List<User> findAttendeesByChannel(UUID channelId) {
        Channel channel = this.find(channelId).channel();
        List<User> attendees = new ArrayList<>();
//        channel.getAttendees().forEach((userId -> {
//            attendees.add(this.userService.find(userId).userId());
//        }));

        return attendees;
    }
}
