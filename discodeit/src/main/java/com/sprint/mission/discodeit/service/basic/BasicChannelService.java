package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final UserRepository userRepository;



    @Override
    public Channel create(CreatePublicChannelRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC,request.name(),request.description());
        request.participantId().ifPresent(channel::addParicipant);
        channel.getParicipantIds()
                .stream()
                .map(userId -> new ReadStatus(userId, channel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);
        return channelRepository.save(channel);
    }

    @Override
    public Channel create(CreatePrivateChannelRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE,null,null);
        channel.addParicipant(request.participantId1());
        channel.addParicipant(request.participantId2());
        channel.getParicipantIds()
                .stream()
                .map(userId -> new ReadStatus(userId, channel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);
        return channelRepository.save(channel);

    }

    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId){
        // private 채널 중에서 userID가 있는 채널만 추출
        List<Channel> channelListAll = new ArrayList<>(channelRepository.findAllByUserId(userId)
                .stream()
                .filter(channel -> channel.getType().equals(ChannelType.PRIVATE))
                .filter(channel -> channel.getParicipantIds().contains(userId))
                .toList());

        // 그 외 모든 채널 추가
        channelRepository.findAll()
                .stream()
                .filter(channel -> channel.getType().equals(ChannelType.PUBLIC))
                .forEach(channelListAll::add);

        return channelListAll.stream().toList();
    }



    @Override
    public Channel update(UUID channelId, UpdateChannelRequest updateChannelRequest) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if(channel.getType().equals(ChannelType.PRIVATE)){
            throw new IllegalArgumentException("Private 채널은 수정할 수 없습니다!");
        }

        channel.update(updateChannelRequest.newName(),updateChannelRequest.newDescription());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        channelRepository.deleteById(channelId);
        messageRepository.deleteByChannelId(channelId);   //   피드백 2 -> 요구사항에 따른 채널 관련 message도 삭제하는 부분
        readStatusRepository.deleteByChannelId(channelId);  //   '' -> 요구사항에 따른 채널 관련 ReadStatus도 함께 삭제하는 부분
    }

    @Override
    public void addParticipant(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        channel.addParicipant(userId);
        System.out.println("add participant : " + userId + " success.");
        channelRepository.save(channel);
    }

    @Override
    public void deleteParticipant(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        //관련 도메인 싹 다 삭제
        channel.deleteParicipant(userId);
        messageRepository.findAllByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        System.out.println("delete participant : " + userId + " success.");
        channelRepository.save(channel);

    }

}
