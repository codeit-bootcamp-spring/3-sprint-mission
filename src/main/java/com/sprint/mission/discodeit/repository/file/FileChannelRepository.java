package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {

    @Value("${discodeit.repository.file-directory}")
    private String FILE_DIRECTORY;
    private final String FILENAME = "channelRepo.ser";
    private final Map<UUID, Channel> data =new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        data.putAll(loadFile());
    }

    private File getFile() {
        return new File(FILE_DIRECTORY, FILENAME);
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);

        saveFile();

        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        Optional<Channel> foundChannel = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(channelId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundChannel;
    }

    @Override
    public List<Channel> findByPrivateChannelUserId(UUID userId) {
        return data.values().stream()
                .filter(channel -> channel.getUsers().contains(userId) && channel.isPrivate())
                .toList();
    }

    @Override
    public List<Channel> findByNameContaining(String name) {
        return data.values().stream()
                .filter(channel -> !channel.isPrivate() && channel.getChannelName().contains(name))
                .toList();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID channelId) {
        data.remove(channelId);
        // Channel 삭제 후 파일에 덮어쓰기
        saveFile();
    }

    private Map<UUID, Channel> loadFile() {
        Map<UUID, Channel> data = new HashMap<>();

        if (getFile().exists()) {
            try (FileInputStream fis = new FileInputStream(getFile());
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, Channel>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    private synchronized void saveFile() {
        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
