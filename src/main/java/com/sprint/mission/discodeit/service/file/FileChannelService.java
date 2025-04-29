package com.sprint.mission.discodeit.service.file;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelService implements ChannelService {
    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/file/data/channel.txt";
    private final Path path = Paths.get(FILE_PATH);

    private Map<UUID, Channel> data;

    public FileChannelService() {
        this.data = new HashMap<>();
    }

    public void saveChannel(List<Channel> channels) { // 객체 직렬화
        try ( // 길 뚫어주고
              FileOutputStream channel = new FileOutputStream(FILE_PATH); // file 주소를 어떻게 설정할까
              ObjectOutputStream channelOOS = new ObjectOutputStream(channel);
              // ObjectOutputStream userOOS = new ObjectOutputStream(new FileOutputStream(new File("user.ser")));
        ) {
            channelOOS.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화 : 조회
    public List<Channel> loadChannels(Path path) {
        List<Channel> channels = new ArrayList<>();

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try (
                ObjectInputStream channelOIS = new ObjectInputStream(new FileInputStream(FILE_PATH));
        ) {
            for (Channel channel : (List<Channel>) channelOIS.readObject()) {
                channels.add(channel);
            }

            return channels;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public Channel createChannel(String channelName, String description) {
        Channel channel = new Channel(channelName, description); // 파라미터로 받아온 user의 정보로 객체 직접 생성
        this.data.put(channel.getId(), channel);

        List<Channel> channels = new ArrayList<>(data.values());

        saveChannel(channels);

        return channel;
    }

    @Override
    public Channel readChannel(UUID id) { // R
        List<Channel> channels = loadChannels(path); // 채널 리스트 역직렬화 // 객체 하나하나 역직렬화 아님

        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                System.out.println("채널 정보 출력");
                System.out.println("===================");
                System.out.println("채널이름: " + channel.getChannelName());
                System.out.println("채널정보: " + channel.getDescription());
                return channel;
            }
        }

        System.out.println("해당 ID를 가진 채널을 찾을 수 없습니다.");
        return null; // 없으면 null, 예외 처리는 아직 못했음
    }

    @Override
    public List<Channel> readAllChannels() { // R 전체 조회
        return loadChannels(path);
    }

    @Override
    public Channel updateChannel(UUID id, String newName, String newDescription) { // U
        Channel channelNullable = this.data.get(id);
        Channel channel = Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException(id + "ID를 가진 채널을 찾을 수 없습니다."));
        channel.updateChannel(newName, newDescription);

        saveChannel(this.data.values().stream().toList());

        return channel;
    }

    @Override
    public void deleteChannel(UUID id) { // D
        if (!this.data.containsKey(id)) {
            throw new NoSuchElementException(id + "ID를 가진 채널을 찾을 수 없습니다.");
        }
        this.data.remove(id);
        saveChannel(this.data.values().stream().toList());
    }
}

