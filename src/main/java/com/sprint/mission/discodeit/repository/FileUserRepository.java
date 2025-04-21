package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final String filePath = "users.dat"; // 데이터를 저장할 파일 경로
    private final Map<UUID, User> storage = new HashMap<>(); // 메모리상의 임시 저장소

    public FileUserRepository() {
        load(); // // 파일에서 데이터 불러오기
    }

    // 사용자 저장
    @Override
    public void save(User user) {
        storage.put(user.getId(), user); // 메모리에 저장
        persist(); // 파일에 반영
    }

    // ID로 사용자 조회
    @Override
    public User findById(UUID id) {
        return storage.get(id); // 메모리에서 조회
    }

    // 전체 사용자 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values()); // 저장된 모든 사용자 반환
    }

    // 사용자 정보 수정
    @Override
    public void update(User user) {
        storage.put(user.getId(), user); // 기존 유저 덮어쓰기
        persist(); // 변경사항을 파일에 저장
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        storage.remove(id); // 메모리에서 삭제
        persist(); // 변경사항을 파일에 반영
    }

    // 파일로 저장 (직렬화)
    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(storage); // Map<UUID, User>를 파일에 저장
        } catch (IOException e) {
            e.printStackTrace(); // 예외 발생 시 콘솔 출력
        }
    }

    // 파일에서 불러오기 (역직렬화)
    private void load() {
        File file = new File(filePath);
        if (!file.exists()) return; // 파일이 없으면 아무것도 안 함

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();

            // 파일에서 읽은 객체가 Map이라면, User로 캐스팅하여 storage에 다시 저장
            if (obj instanceof Map<?, ?>) {
                Map<?, ?> loaded = (Map<?, ?>) obj;
                for (Object key : loaded.keySet()) {
                    if (key instanceof UUID && loaded.get(key) instanceof User) {
                        storage.put((UUID) key, (User) loaded.get(key));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); // 예외 발생 시 콘솔 출력
        }
    }
}