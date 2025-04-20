package com.sprint.mission.discodeit.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

// UserServiceFile 사용자 서비스
public class FileUserService implements UserService {

    // 저장할 파일 경로
    private static final String FILE_PATH = "users.ser";

    // 사용자 정보를 저장할 Map, 시작 시 파일에서 불러옴
    private Map<UUID, User> users = load();

    // 사용자 생성 및 저장
    @Override
    public User create(String name) {
        User user = new User(name);            // 새로운 사용자 생성
        users.put(user.getId(), user);         // Map에 저장
        save();                                // 파일에 저장
        return user;
    }

    // 사용자 ID로 조회
    @Override
    public User findById(UUID id) {
        return users.get(id);
    }

    // 모든 사용자 목록 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    // 사용자 이름 수정
    @Override
    public User update(UUID id, String newName) {
        User user = users.get(id);
        if (user != null) {
            user.updateName(newName);  // 이름 변경
            save();                    // 변경 내용 저장
        }
        return user;
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        users.remove(id);  // Map에서 제거
        save();            // 파일에 저장 반영
    }

    // Map 데이터를 파일로 직렬화하여 저장
    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(users);  // 전체 사용자 Map을 파일에 저장
        } catch (IOException e) {
            e.printStackTrace();    // 저장 실패 시 에러 출력
        }
    }

    // Map 데이터를 역직렬화하여 불러오기
    private Map<UUID, User> load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, User>) in.readObject();  // 저장된 Map 복원
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();  // 파일이 없거나 실패하면 빈 Map 반환
        }
    }
}
