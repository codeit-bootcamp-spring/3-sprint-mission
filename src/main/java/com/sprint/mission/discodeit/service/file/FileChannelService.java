package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {
    // 채널 정보가 저장될 파일 경로 설정
    private static final String CHANNEL_FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/data/channel.txt";

    // 채널 리스트 불러오기
    private List<Channel> loadChannels() {
        File file = new File(CHANNEL_FILE_PATH);
        if (!file.exists()) {     // 방어 코드
            return new ArrayList<>();     // 새로운 ArrayList 반환
        }

        // 객체 역직렬화
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();     // 예외처리 로그 출력
            return new ArrayList<>();     // 예외 발생 시 빈 리스트 반환
        }
    }


    // 채널 리스트 저장
    private void saveChannel(List<Channel> channels) {

        // 객체 직렬화
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CHANNEL_FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();     // 예외처리 로그 출력
        }
    }


    @Override
    public void createChannel(Channel channel) {
        List<Channel> channels = loadChannels();
        channels.add(channel);
        saveChannel(channels);
        System.out.println("Channel created");
    }

    @Override
    public Channel readChannel(UUID id) {
        return loadChannels().stream()
                .filter(channel -> channel.getChannelId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> readChannelByName(String name) {
        return loadChannels().stream()
                .filter(channel -> channel.getChannelName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> readChannelByType(String type) {
        return loadChannels().stream()
                .filter(channel -> channel.getChannelType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> readAllChannels() {
        return loadChannels();
    }

    @Override
    public Channel updateChannel(UUID id, Channel channel) {
        List<Channel> channels = loadChannels();
        for (int i = 0; i < channels.size(); i++) {     // 최대 크기만큼 인덱스를 돌려가며 ID가 일치하는 대상의 정보 수정
            if (channels.get(i).getChannelId().equals(id)) {
                channels.set(i, channel);
                saveChannel(channels);
                return channel;
            }
        }
        return null;
    }

    @Override
    public boolean deleteChannel(UUID id) {
        List<Channel> channels = loadChannels();
        // 일치하는 ID의 채널 삭제 시도( 있다면 True, 없다면 false )
        boolean removed = channels.removeIf(channel -> channel.getChannelId().equals(id));
        if (removed) {     // 해당 ID 존재하여 삭제 시
            saveChannel(channels);     // 변경 정보를 저장 ( 삭제 반영 )
        }
        return removed;
    }
}
