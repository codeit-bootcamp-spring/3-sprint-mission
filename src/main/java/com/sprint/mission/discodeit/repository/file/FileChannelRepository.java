package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileChannelRepository implements ChannelRepository {

    private final String FILENAME = "src/main/java/com/sprint/mission/discodeit/data/channelRepo.ser";
    private final File file = new File(FILENAME);

    @Override
    public Channel save(Channel channel) {
        // 파일로 부터 읽어오기
        Map<UUID, Channel> data = loadFile();

        data.put(channel.getId(), channel);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        // 파일로 부터 읽어오기
        Map<UUID, Channel> data = loadFile();

        Optional<Channel> foundChannel = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(channelId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundChannel;
    }

    @Override
    public List<Channel> findByPrivateChannelUserId(UUID userId) {
        // 파일로 부터 읽어오기
        Map<UUID, Channel> data = loadFile();

        return data.values().stream()
                .filter(channel -> channel.getUsers().contains(userId) && channel.isPrivate())
                .toList();
    }

    @Override
    public List<Channel> findByNameContaining(String name) {
        // 파일로 부터 읽어오기
        Map<UUID, Channel> data = loadFile();

        return data.values().stream()
                .filter(channel -> !channel.isPrivate() && channel.getChannelName().contains(name))
                .toList();
    }

    @Override
    public List<Channel> findAll() {
        // 파일로 부터 읽어오기
        Map<UUID, Channel> data = loadFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID channelId) {
        // 파일로 부터 읽어오기
        Map<UUID, Channel> data = loadFile();

        data.remove(channelId);

        // Channel 삭제 후 파일에 덮어쓰기
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, Channel> loadFile() {
        Map<UUID, Channel> data = new HashMap<>();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, Channel>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
