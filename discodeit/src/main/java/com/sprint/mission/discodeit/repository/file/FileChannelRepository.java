package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileioUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Map<String, Channel> channelData;
    private final Path path;

    public FileChannelRepository(@Qualifier("channelFilePath") Path path) {
        this.path = path;
        if(!Files.exists(this.path)){
            try{
                Files.createFile(this.path);
                FileioUtil.save(this.path, new HashMap<>());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileioUtil.init(this.path);
        this.channelData = FileioUtil.load(this.path, Channel.class);

    }


    @Override
    public Channel save(Channel channel) {
        channelData.put(channel.getId().toString(), channel);
        FileioUtil.save(this.path, channelData);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        if(!channelData.containsKey(id.toString())){
            throw new NoSuchElementException("Channel not found with id " + id);
        }
        return Optional.ofNullable(channelData.get(id.toString()));
    }

    @Override
    public List<Channel> findAll() {
        return channelData.values().stream().toList();
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return channelData.values()
                .stream()
                .filter(channel -> channel.getParicipantIds().contains(userId)).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return channelData.containsKey(id.toString());
    }

    @Override
    public void deleteById(UUID id) {
        channelData.remove(id.toString());
        FileioUtil.save(this.path, channelData);
    }
}
