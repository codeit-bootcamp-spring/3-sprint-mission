package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.FindChannelRespond;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;
import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.ChannelType;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;


    @Override
    public Channel create(CreatePublicChannelRequest createPublicChannelRequest) {
        Channel channel = new Channel(createPublicChannelRequest.channelName(), ChannelType.PUBLIC, createPublicChannelRequest.description());
        return channelRepository.save(channel);

    }

    @Override
    public Channel create(CreatePrivateChannelRequest createPrivateChannelRequest) {
        Channel channel = new Channel(null,ChannelType.PRIVATE,null);
        createPrivateChannelRequest.users().stream()
                .forEach((user)->{
                    readStatusRepository.save(new ReadStatus(user.getId(),channel.getId()));
                });
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public List<FindChannelRespond> findAllByUserId(UUID userId) {
        //readStatusRepository에서 해당 User가 속한 channel의 id를 List로 모아서 반환
        List<UUID> channelsUserBelongsTo = readStatusRepository.readByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();
        //반환배열 생성
        List<FindChannelRespond> findChannelRespondList = new ArrayList<>();
        List<Channel> channelList = channelRepository.read();
        for(Channel channel:channelList){
            //Channel Type이 Private이면서 유저가 속한 ChannelList에 채널의 id가 있다면 조회
            if(channel.getType().equals(ChannelType.PRIVATE) && channelsUserBelongsTo.contains(channel.getId()) ){
                //해당 채널의 가장 최근 메시지의 시간 정보 찾기
                Instant latestReadTime = Instant.MIN;
                List<Message> messageList = messageRepository.readByChannelId(channel.getId());
                for(Message message:messageList){
                    Instant createdAt = message.getCreatedAt();
                    if(createdAt.isAfter(latestReadTime)){
                        latestReadTime = createdAt;
                    }
                }
                //채널에 속한 유저의 IdList 만들기
                List<UUID> userIdList = readStatusRepository.readByChannelId(channel.getId()).stream()
                        .map(readStatus -> readStatus.getUserId())
                        .toList();
                findChannelRespondList.add(new FindChannelRespond(null,null,ChannelType.PRIVATE,latestReadTime,userIdList));

            } //Channel Type이 Public이라면 전체 조회
            else if(channel.getType().equals(ChannelType.PUBLIC)){
                //해당 채널의 가장 최근 메시지의 시간 정보 찾기
                Instant latestReadTime = Instant.MIN;
                List<Message> messageList = messageRepository.readByChannelId(channel.getId());
                for(Message message:messageList){
                    Instant createdAt = message.getCreatedAt();
                    if(createdAt.isAfter(latestReadTime)){
                        latestReadTime = createdAt;
                    }
                }
                findChannelRespondList.add(new FindChannelRespond(channel.getChannelName(),channel.getDescription(),ChannelType.PUBLIC,latestReadTime,null));
            }
        }
        return findChannelRespondList;
    }

    @Override
    public FindChannelRespond find(UUID id) {
        Optional<Channel> channel = channelRepository.readById(id);
        if(channel.isPresent()){
            Channel findChannel = channel.get();
            //해당 채널의 가장 최근 메시지의 시간 정보 찾기
            Instant latestReadTime = Instant.MIN;
            List<Message> messageList = messageRepository.readByChannelId(findChannel.getId());
            for(Message message:messageList){
                Instant createdAt = message.getCreatedAt();
                if(createdAt.isAfter(latestReadTime)){
                    latestReadTime = createdAt;
                }
            }
            //Channel Type이 Private일경우
            if(findChannel.getType().equals(ChannelType.PRIVATE)){
                //readStatusRepository에서 ChannelId가 일치하는 readStatus를 찾아 해당 User의 Id를 List로 만들어 반환.
                List<UUID> userIdList = readStatusRepository.readByChannelId(findChannel.getId()).stream()
                        .map(ReadStatus::getUserId)
                        .toList();
                return new FindChannelRespond(null,null,ChannelType.PRIVATE,latestReadTime,userIdList);
            }//Public일 경우
            else if(findChannel.getType().equals(ChannelType.PUBLIC)){
                return new FindChannelRespond(findChannel.getChannelName(),findChannel.getDescription(),ChannelType.PUBLIC,latestReadTime,null);
            }
        } else{
            try {
                throw new NoSuchElementException("해당 id는 존재하지 않습니다.");
            } catch (NoSuchElementException e) {
                System.out.println(e);
            }
        }
        return null;
    }

    @Override
    public void update(UpdateChannelRequest request) {
        //Id로 채널을 검색
        Optional<Channel> channel = channelRepository.readById(request.id());

        //채널이 존재하면서 채널의 타입이 Public이라면 update 수행
        if(channel.isPresent() && channel.get().getType().equals(ChannelType.PUBLIC)){
            Channel updateChannel = channel.get();
            updateChannel.setUpdatedAt(Instant.now());
            updateChannel.setChannelName(request.channelName());
            updateChannel.setDescription(request.description());
            channelRepository.update(request.id(),updateChannel);
        };

    }

    @Override
    public void delete(UUID channelId) {
        //채널 id로 검색
        List<Message> messages = messageRepository.readByChannelId(channelId);
        List<ReadStatus> readStatuses = readStatusRepository.readByChannelId(channelId);

        //채널 id가 일치하는 메세지, readStatus 삭제
        for(Message message:messages)
            messageRepository.delete(message.getId());
        for(ReadStatus readStatus:readStatuses)
            readStatusRepository.delete(readStatus.getId());

        channelRepository.delete(channelId);
    }
}
