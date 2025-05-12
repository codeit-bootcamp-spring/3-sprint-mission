package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
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

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel create(PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = new Channel(ChannelType.PUBLIC, publicChannelCreateRequest.ownerId(), publicChannelCreateRequest.name(), publicChannelCreateRequest.description());
        return this.channelRepository.save(channel);
    }

    /* User 별 ReadStatus 생성 , name과 description 생략 */
    public Channel create(PrivateChannelCreateRequest privateCreateRequest) {
        Channel channel = new Channel(ChannelType.PRIVATE, privateCreateRequest.ownerId(), null, null);

        this.readStatusRepository.save(new ReadStatus(privateCreateRequest.ownerId(), channel.getId()));

        /* attendeeIds 에 있는 유저들 ReadStatus 생성 */
        for (UUID attendeeId : privateCreateRequest.attendeeIds()) {
            this.readStatusRepository.save(new ReadStatus(attendeeId, channel.getId()));
        }

        return this.channelRepository.save(channel);
    }

    @Override
    public ChannelDto find(UUID channelId) {
        return this.channelRepository
                .findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        // get private channel by userId
        List<UUID> mySubscribedChannelIds = this.readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatus::getChannelId)
                .toList();

        System.out.println("mySubscribedChannelIds = " + mySubscribedChannelIds);

        // get public + private channel
        List<ChannelDto> myChannels = this.channelRepository.findAll()
                .stream()
                .filter((channel -> channel.getType().equals(ChannelType.PUBLIC) || mySubscribedChannelIds.contains(channel.getId())))
                .map(this::toDto)
                .toList();

        return myChannels;
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest updateRequest) {
        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("private 채널은 수정이 불가능합니다.");
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
        this.messageRepository.deleteAllByChannelId(channel.getId());

        /* 해당 채널과 관련된 모든 ReadStatus 삭제 */
        this.readStatusRepository.deleteAllByChannelId(channel.getId());

        this.channelRepository.deleteById(channel.getId());
    }

    private ChannelDto toDto(Channel channel) {
        List<UUID> attendeeIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            attendeeIds = this.findAttendeeIdsByChannel(channel.getId());
        }

        return new ChannelDto(channel.getId(), channel.getName(), channel.getDescription(), channel.getOwnerId(), attendeeIds, channel.getLastMessageAt());
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
//        Channel channel = this.channelRepository
//                .findById(channelId)
//                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
//        channel.removeAttendee(userId);
//        this.create(channel);
    }

    //TODO
    @Override
    public List<UUID> findAttendeeIdsByChannel(UUID channelId) {
        Channel channel = this.channelRepository
                .findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        return this.readStatusRepository.findAllByChannelId(channel.getId()).stream().map((ReadStatus::getUserId)).toList();
    }
}
