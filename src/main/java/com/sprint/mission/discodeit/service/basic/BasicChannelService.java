package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel create(PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = new Channel(publicChannelCreateRequest);

        this.channelRepository.save(channel);

        return channel;
    }

    /* User 별 ReadStatus 생성 , name과 description 생략 */
    public Channel create(PrivateChannelCreateRequest privateCreateRequest) {
        Channel channel = new Channel(privateCreateRequest);

        /* attendees 에 있는 유저들 ReadStatus 생성 */
        for (UUID attendeeId : channel.getAttendees()) {
            this.readStatusRepository.save(new ReadStatus(attendeeId, channel.getId()));
        }

        this.channelRepository.save(channel);

        return channel;
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = this.channelRepository
                .findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        List<UUID> attendees = channel.getType().equals(ChannelType.PRIVATE) ? channel.getAttendees() : null;

        return new ChannelResponse(channel, channel.getLastMessageTime(), attendees);
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
            return new ChannelResponse(channel, channel.getLastMessageTime(), attendees);
        }).toList();

        return channelResponses;
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest updateRequest) {
        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new UnsupportedOperationException("private 채널은 수정이 불가능합니다.");
        }

        channel.update(updateRequest.newName(), updateRequest.newDescription());

        /* 업데이트 후 다시 DB 저장 */
        this.channelRepository.save(channel);

        Channel updatedChannel = this.channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        return updatedChannel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = this.channelRepository.findById(channelId).
                orElseThrow(() -> new NoSuchElementException("Channel with channelId " + channelId + " not found"));

        /* 해당 채널과 관련된 모든 Message 삭제 */
        List<Message> messages = this.messageRepository.findAllByChannelId(channelId);
        for (Message message : messages) {
            this.messageRepository.deleteById(message.getId());
        }
        /* 해당 채널과 관련된 모든 ReadStatus 삭제 */
        List<ReadStatus> readStatuses = this.readStatusRepository.findAllByChannelId(channelId);
        for (ReadStatus readStatus : readStatuses) {
            this.readStatusRepository.deleteById(readStatus.getId());
        }

        this.channelRepository.deleteById(channelId);
    }

    //TODO
    @Override
    public void addMessageToChannel(UUID channelId, UUID messageId) {
//        Channel channel = this.find(channelId).channel();
//        channel.addMessage(messageId);
//        this.create(channel);
    }

    //TODO
    @Override
    public void addAttendeeToChannel(UUID channelId, UUID userId) {
//        Channel channel = this.find(channelId).channel();
//        channel.addAttendee(userId);
//        this.create(channel);
    }

    //TODO
    @Override
    public void removeAttendeeToChannel(UUID channelId, UUID userId) {
        Channel channel = this.find(channelId).channel();
        channel.removeAttendee(userId);
//        this.create(channel);
    }

    //TODO
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
