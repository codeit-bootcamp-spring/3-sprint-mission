package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private static final Path FILE_PATH = Paths.get("data/channel.ser");
    private final Map<UUID, Channel> data = getChannels();

    /**
     * 채널명을 인자로 받아 채널을 생성하는 메서드
     *
     * @param channelName 생성할 채널명
     * @return 생성된 채널
     * @throws IllegalArgumentException 중복 이름인 채널이 존재하는 경우
     */
    @Override
    public Channel createChannel(String channelName) {
        // 중복 이름인 채널 생성 불가
        for (Channel channel : data.values()) {
            if (channel.getChannelName().equals(channelName)) {
                throw new IllegalArgumentException("이미 존재하는 채널입니다. 다른 채널명을 입력해주세요.");
            }
        }
        // 채널 생성 및 컬렉션에 추가
        Channel ch = new Channel(channelName);
        data.put(ch.getId(), ch);
        saveChannels();
        return ch;
    }

    /**
     * 파일에서 읽어온 채널 데이터를 역직렬화하여 로드하는 메서드
     *
     * @return 파일에 저장된 채널 데이터
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<UUID, Channel> getChannels() {
        if (!Files.exists(FILE_PATH)) {
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))
        ) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 주어진 id에 해당하는 채널을 조회하는 메서드
     *
     * @param id 조회할 채널의 ID
     * @return 조회된 채널
     * @throws NoSuchElementException 해당 ID의 채널이 존재하지 않는 경우
     */
    @Override
    public Channel getChannelById(UUID id) {
        Channel ch = data.get(id);
        if (ch == null) {
            throw new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다.");
        }
        return ch;
    }

    /**
     * 주어진 채널명에 해당하는 채널을 조회하는 메서드
     *
     * @param channelName 조회할 채널명
     * @return 조회된 채널
     * @throws NoSuchElementException 해당 이름의 채널이 존재하지 않는 경우
     */
    @Override
    public Channel getChannelByChannelName(String channelName) {
        // data를 순회하며 채널명으로 검색
        return data.values().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
    }

    /**
     * 주어진 채널을 새로운 채널명으로 수정하는 메서드
     *
     * @param channel 수정할 대상 채널
     * @param channelName 새로운 채널명
     * @return 수정된 채널
     */
    @Override
    public Channel updateChannel(Channel channel, String channelName) {
        Channel ch = getChannelById(channel.getId());
        // 채널 수정
        ch.updateChannelName(channelName);
        saveChannels();
        return ch;
    }

    /**
     * 주어진 id에 해당하는 채널을 삭제하는 메서드
     *
     * @param id 삭제할 대상 채널 id
     * @return 삭제된 채널
     */
    @Override
    public Channel deleteChannel(UUID id) {
        Channel targetChannel = getChannelById(id);
        // 채널 컬렉션에서 삭제
        data.remove(id);
        saveChannels();
        return targetChannel;
    }

    /**
     * 채널 데이터를 직렬화하여 파일에 저장하는 메서드
     *
     * @throws RuntimeException 파일 생성/직렬화 중 예외가 발생한 경우
     */
    public void saveChannels() {
        try{
            Files.createDirectories(FILE_PATH.getParent());
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))){
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
