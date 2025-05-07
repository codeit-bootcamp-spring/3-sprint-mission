package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "JCF", matchIfMissing = true)
public class JCFChannelRepository implements ChannelRepository {

    private final CopyOnWriteArrayList<Channel> data  = new CopyOnWriteArrayList<>();

    @Override
    public Channel save(Channel channel) {
        data.add(channel);
        return channel;
    }

    @Override
    public List<Channel> read() {
        return data;
    }

    @Override
    public Optional<Channel> readById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findAny();
    }

    @Override
    public void update(UUID id, Channel channel) {
        data.stream()
                .filter(chan -> chan.getId().equals(id))
                .forEach(chan-> {
                    chan.setUpdatedAt(Instant.now());
                    chan.setChannelName(channel.getChannelName());
                    chan.setDescription(channel.getDescription());
                    chan.setType(channel.getType());
                });
    }

    @Override
    public void delete(UUID channelId) {
        data.removeIf(channel -> channel.getId().equals(channelId));
    }

}
