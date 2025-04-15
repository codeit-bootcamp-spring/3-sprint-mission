package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

// JCF 활용, 데이터를 저장할 수 있는 필드(data)를 final 로 선언, 생성자에서 초기화
// data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현( < 오버라이드? )
public class JCFUserService implements UserService {
    // data 필드 final 선언 및 캡슐화 ( JCF : 고유 식별 코드(UUID)에 따른 값(User) 탐색을 위한 Map 차용 )
    private final Map<UUID, User> data;


    // 생성자 초기화
    public JCFUserService(Map<UUID, User> data) {
        if (data == null || data.isEmpty()) {       // 방어 코드 작성
            this.data = new HashMap<>();
        } else {
            this.data = data;
        }
    }

    // Create Override
    @Override
    public void createUser(User user) {     // 새로운 사용자 생성
        data.put(user.getUserId(), user);
    }

    // Read Override
    @Override
    public User readUser(UUID id) {     // 고유 식별 코드를 이용한 단일 조회
        return data.get(id);
    }

    @Override
    public List<User> readUserByName(String name) {     // 특정 이름과 일치하는 유저 다중 조회
        return data.values().stream()                   // Stream API 적용
                .filter(user -> user.getUserName().equals(name))   // 람다식 적용(유저 목록 중 찾고자하는 이름과 동일한 이름의 유저만 필터링
                .collect(Collectors.toList());  // 해당 정보를 List 자료형으로 저장
    }

    @Override
    public List<User> readAllUsers() {      // 모든 사용자 전체 조회
        return new ArrayList<>(data.values());
    }

    // Update Override
    @Override
    public User updateUser(UUID id, User user) {
        if (data.containsKey(id)) {     // 특정 키(id)가 존재하는지 검색
            data.put(id, user);         // 정보 덮어씌우기
            return user;
        }
        return null;
    }

    // Delete Override
    @Override
    public boolean deleteUser(UUID id) {
        return data.remove(id) != null;     // 특정 사용자 정보 제거
    }
}
