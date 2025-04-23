package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;

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

    private final UserRepository userRepository;

    public FileUserService() {
        this.userRepository = new FileUserRepository();
    }

    @Override
    public User createUser(String name) {
        return userRepository.save(new User(name));
    }

    @Override
    public User getUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user, String newName) {
        user.updateName(newName);
        return userRepository.save(user);
    }

    @Override
    public User deleteUser(User user) {
        return userRepository.delete(user);
    }
}
