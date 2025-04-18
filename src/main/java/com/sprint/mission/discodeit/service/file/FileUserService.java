package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

/**
 * FileUserService
 * - File 기반 영속 저장소를 사용하는 UserService 구현체
 * - JCFUserService와 비교하여 다음과 같은 공통점과 차이점이 존재
 *
 * [공통점]
 * UserService 인터페이스를 구현한다.
 * 동일한 CRUD 메서드 시그니처를 가진다.
 * 비즈니스 로직은 동일하게 작동한다.
 *
 * [차이점]
 * 저장 방식이 다르다:
 * - JCFUserService는 메모리 상의 HashMap에만 저장
 * - FileUserService는 저장 이후 파일로 직렬화하여 영속화
 *
 * 초기화 방식이 다르다:
 * - JCFUserService는 빈 Map으로 시작
 * - FileUserService는 파일에서 `store.load()`로 데이터 복원
 *
 * 책임 분리가 명확해진다:
 * - FileUserService는 비즈니스 로직과 저장 로직이 구분되어 있다.
 * - JCFUserService는 Map에 의존
 *
 */
public class FileUserService implements UserService {
    private final FileDataStore<User> store;
    private final Map<UUID, User> data;

    public FileUserService() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        this.store = new FileDataStore<>("data/users.ser");
        this.data = store.load();
    }

    @Override
    public User createUser(String name) {
        User user = new User(name);
        data.put(user.getId(), user);   // 비즈니스 로직
        store.save(data);               // 저장 로직
        return user;
    }

    @Override
    public User getUser(UUID id) {
        User user = data.get(id);
        if (user == null) throw new NoSuchElementException("User not found: " + id);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(User user, String newName) {
        user.updateName(newName);   // 비즈니스 로직
        store.save(data);           // 저장 로직
        return user;
    }

    @Override
    public User deleteUser(User user) {
        User removed = data.remove(user.getId());   // 비즈니스 로직
        store.save(data);                           // 저장 로직
        return removed;
    }
}
