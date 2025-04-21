package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final String filePath = "messages.dat"; // 저장 파일 경로
    private final Map<UUID, Message> storage = new HashMap<>(); // 메모리 내 저장소

    public FileMessageRepository() {
        load(); // 파일에서 데이터 불러오기
    }

    // 메시지 저장
    @Override
    public void save(Message message) {
        storage.put(message.getId(), message); // ID 기준으로 저장 (중복 ID면 덮어쓰기)
        persist();                             // 변경 사항을 파일에 반영
    }

    // ID로 메시지 조회
    @Override
    public Message findById(UUID id) {
        return storage.get(id); // 해당 ID의 메시지를 반환 (없으면 null)
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(storage.values()); // 저장된 메시지들을 리스트로 반환
    }

    // 메시지 업데이트
    @Override
    public void update(Message message) {
        storage.put(message.getId(), message); // 동일 ID 기준으로 덮어쓰기
        persist();                             // 파일에 변경 내용 저장
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        storage.remove(id);
        persist();
    }

    // 파일 저장
    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(storage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일 불러오기
    private void load() {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> map) {
                for (Object key : map.keySet()) {
                    if (key instanceof UUID uuid && map.get(key) instanceof Message message) {
                        storage.put(uuid, message);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}