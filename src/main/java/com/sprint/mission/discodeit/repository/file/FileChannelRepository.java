package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static final String CHANNEL_FILE_REPOSITORY_PATH = "src/main/java/com/sprint/mission/discodeit/repository/data/channel.txt";
    File file = new File(CHANNEL_FILE_REPOSITORY_PATH);

    @Override
    public void save(Channel channel) {

        // 방어 코드
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 객체 직렬화( 저장 )
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAll(List<Channel> channels) {

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 객체 직렬화( 저장 )
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Channel> loadAll() {

        // 객체 역직렬화
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CHANNEL_FILE_REPOSITORY_PATH))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Channel loadById(UUID id) {
        List<Channel> channels = loadAll();
        for (Channel channel : channels) {
            if (channel.getChannelId().equals(id)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> loadByName(String name) {
        List<Channel> channels = loadAll();
        for (Channel channel : channels) {
            if (channel.getChannelName().equals(name)) {
                return channels;
            }
        }
        return null;
    }

    @Override
    public List<Channel> loadByType(String type) {
        List<Channel> channels = loadAll();
        for (Channel channel : channels) {
            if (channel.getChannelType().equals(type)) {
                return channels;
            }
        }
        return null;
    }
}
