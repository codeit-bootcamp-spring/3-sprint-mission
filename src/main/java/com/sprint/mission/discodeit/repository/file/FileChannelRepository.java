package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository("fileChannelRepository")
public class FileChannelRepository implements ChannelRepository {
    private static final String DIR = "data/channels/";

    public FileChannelRepository() {
        clearFile();
    }

    @Override
    public void save(Channel channel) {
        try (
                FileOutputStream fos = new FileOutputStream(DIR + channel.getId() + ".ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel loadByName(String name) {
        List<Channel> channels = loadAll();

        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public Channel loadById(UUID id) {
        if (!Files.exists(Path.of(DIR))) { return null; }

        try (
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(DIR + id + ".ser"))
        ) {
            return (Channel) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<Channel> loadAll() {
        if (Files.exists(Path.of(DIR))) {
            try {
                List<Channel> channels = Files.list(Paths.get(DIR))
                        .map( path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (Channel) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return channels;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void update(UUID id, String name) {
        Channel channel = loadById(id);
        if (channel == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널입니다. (" + id + ")");
        }

        channel.setName(name);
        save(channel);
    }

    @Override
    public void join(UUID userId, UUID channelId) {
        Channel channel = loadById(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("[User] 유효하지 않은 channel 파일 (" + channelId + ".ser)");
        }

        channel.join(userId);
        save(channel);
        System.out.println("[Channel] 채널에 접속했습니다. (userId: " + userId + ", channelId: " + channelId + ")");
    }

    @Override
    public void leave(UUID userId, UUID channelId) {
        Channel channel = loadById(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("[User] 유효하지 않은 channel 파일 (" + channelId + ".ser)");
        }

        channel.leave(userId);
        save(channel);
        System.out.println("[Channel] 채널에서 탈퇴했습니다. (userId: " + userId + ", channelId: " + channelId + ")");
    }

    @Override
    public void delete(UUID id) {
        File file = new File(DIR + id + ".ser");

        try {
            if (file.exists()) {
                if (!file.delete()) {
                    System.out.println("[Channel] 파일 삭제 실패");
                };
            }
            else {
                System.out.println("[Channel] 유효하지 않은 파일 (" + id + ")");
            }
        } catch (Exception e) {
            throw new RuntimeException("[Channel] 파일 삭제 중 오류 발생 (" + id + ")", e);
        }
    }

    private void clearFile() {
        File dir = new File(DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) { return; }

            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    throw new RuntimeException("[Channel] channels 폴더 초기화 실패", e);
                }
            }
        }
    }
}
