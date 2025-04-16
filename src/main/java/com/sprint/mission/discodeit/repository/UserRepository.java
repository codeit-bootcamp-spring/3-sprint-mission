package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;


public interface UserRepository {

    // 저장
    void save(User user);

    // 전체 저장
    void saveAll(List<User> users);

    // 전체 읽기
    List<User> loadAll();

    // 파라미터 타입이 다르니, 간결하게 작성하느냐...
    // 메서드명을 바꿔 가독성을 높이냐... < 채택

    // ID 기반 읽기
    User loadById(UUID id);

    // 이름 기반 읽기
    List<User> loadByName(String name);
}
