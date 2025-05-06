package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.ReadStatusCreateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    public BasicChannelService(ChannelRepository channelRepository, ReadStatusRepository readStatusRepository) {
        this.channelRepository = channelRepository;
        this.readStatusRepository = readStatusRepository;
    }

    @Override
    public Channel create(PrivateChannelCreateDTO dto) throws IOException {
        UUID userId = dto.userId();
        String channelName = dto.channalName();
        List<UUID> userIds = dto.entry();
        Channel channel = new Channel(userId, channelName, true);
        channel.addEntry(userIds);
        this.channelRepository.save(channel);

        for (UUID id : userIds) {
            ReadStatus readStatus = new ReadStatus(id, channel.getId());
            readStatusRepository.save(readStatus);
        }

        return channel;
    }

    @Override
    public Channel create(PublicChannelCreateDTO dto) throws IOException {
        UUID userId = dto.userId();
        String channelName = dto.channalName();

        Channel channel = new Channel(userId, channelName, false);

        this.channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel find(UUID id){

        return this.channelRepository.find(id);
    }

    @Override
    public List<Channel> findByName(String name) {

        return this.channelRepository.findByName(name);
    }

    @Override
    public List<Channel> findAll() {

        return this.channelRepository.findAll();
    }

    @Override
    public void updateName(UUID id, String newname) throws IOException {
        Channel channel = find(id);
        channel.updateName(newname);
        this.channelRepository.save(channel);
    }

    @Override
    public void addEntry(UUID id, UUID userId) {
        Channel channel = find(id);
        channel.addEntry(userId);
        this.channelRepository.save(channel);
    }

    @Override
    public void addEntry(UUID id, List<UUID> userIds) {
        Channel channel = find(id);
        channel.addEntry(userIds);
        this.channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) throws IOException {
        this.channelRepository.delete(id);
    }
}
