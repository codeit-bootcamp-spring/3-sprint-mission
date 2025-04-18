package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static final Path FILE_PATH = Paths.get("data/channel.ser");
    private final Map<UUID, Channel> data;

    public FileChannelRepository() {
        this.data = readAll();
    }

    public FileChannelRepository(Map<UUID, Channel> data) {
        this.data = data;
    }

    /**
     * 채널 데이터를 직렬화하여 파일에 저장하는 메서드
     *
     * @throws RuntimeException 파일 생성/직렬화 중 예외가 발생한 경우
     */
    @Override
    public void saveAll() {
        try{
            Files.createDirectories(FILE_PATH.getParent());
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))){
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("채널 데이터 파일을 저장하는 중 오류가 발생하였습니다.");
        }

    }

    /**
     * 주어진 채널을 파일에 저장하는 메서드
     *
     * @param channel 저장할 채널
     */
    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
        saveAll();
    }

    /**
     * 주어진 id에 해당하는 채널을 삭제하는 메서드
     *
     * @param id 삭제할 대상 채널 id
     */
    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveAll();
    }

    /**
     * 파일에서 읽어온 채널 데이터를 역직렬화하여 로드하는 메서드
     *
     * @return 저장된 채널 데이터
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @SuppressWarnings("unchecked")
    public Map<UUID, Channel> readAll() {
        if (!Files.exists(FILE_PATH)) {
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))
        ) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 데이터 파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 id에 해당하는 채널을 조회하는 메서드
     *
     * @param id 조회할 채널의 ID
     * @return 조회된 채널
     */
    @Override
    public Optional<Channel> readById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    /**
     * 주어진 채널명에 해당하는 채널을 조회하는 메서드
     *
     * @param channelName 조회할 채널명
     * @return 조회된 채널
     */
    @Override
    public Optional<Channel> readByChannelName(String channelName) {
        return data.values().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst();
    }
}
