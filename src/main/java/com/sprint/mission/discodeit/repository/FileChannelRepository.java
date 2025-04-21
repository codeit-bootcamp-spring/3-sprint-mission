package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final String filePath = "channels.dat"; // 저장 파일 경로
    private final Map<UUID, Channel> storage = new HashMap<>(); // 메모리 내 저장소

    public FileChannelRepository() {
        load(); // 파일에서 데이터 불러오기
    }

    // 채널 저장
    @Override
    public void save(Channel channel) {
        storage.put(channel.getId(), channel);
        persist(); // 저장 후 파일에 반영
    }

    // 채널 ID로 조회
    @Override
    public Channel findById(UUID id) {
        return storage.get(id);
    }

    // 전체 채널 조회
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(storage.values());
    }

    // 채널 정보 수정
    @Override
    public void update(Channel channel) {
        storage.put(channel.getId(), channel);
        persist();
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        storage.remove(id);
        persist();
    }

    // 파일 저장 (직렬화)
    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(storage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일 불러오기 (역직렬화)
    private void load() {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?>) {
                Map<?, ?> loaded = (Map<?, ?>) obj;
                for (Object key : loaded.keySet()) {
                    if (key instanceof UUID && loaded.get(key) instanceof Channel) {
                        storage.put((UUID) key, (Channel) loaded.get(key));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}