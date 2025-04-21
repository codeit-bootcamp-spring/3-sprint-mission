package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

// 파일 기반 유저 서비스 구현체 (비즈니스 로직만 담당)
public class FileUserService implements UserService {

    // 저장소 인터페이스 사용 → 구현체는 FileUserRepository
    private final UserRepository userRepository = new FileUserRepository();

    // 사용자 생성
    @Override
    public User create(String name) {
        User user = new User(name);
        userRepository.save(user); // 저장소에 위임
        return user;
    }

    // ID로 사용자 조회
    @Override
    public User findById(UUID id) {
        return userRepository.findById(id); // 저장소에 위임
    }

    // 전체 사용자 목록 조회
    @Override
    public List<User> findAll() {
        return userRepository.findAll(); // 저장소에 위임
    }

    // 사용자 이름 수정
    @Override
    public User update(UUID id, String newName) {
        User user = userRepository.findById(id);
        if (user != null) {
            user.updateName(newName); // 이름 변경
            user.updateUpdatedAt();   // 수정 시간 갱신
            userRepository.update(user); // 저장소에 반영
        }
        return user;
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        userRepository.delete(id); // 저장소에 위임
    }
}
