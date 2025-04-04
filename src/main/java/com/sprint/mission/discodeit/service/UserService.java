package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // 유저 등록
    void create(User user);

    // 유저 단일 조회
    User getById(UUID id);

    // 전체 유저 조회
    List<User> getAll();

    // 유저 정보 수정
    void update(User user);

    // 유저 삭제
    void delete(UUID id);

}
