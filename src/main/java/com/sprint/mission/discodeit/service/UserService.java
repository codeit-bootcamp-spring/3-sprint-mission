package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.UUID;

public interface UserService {
    // 생성
    public User createUser(String userName, String userId);
    // 전체 조회
    public Map<UUID, User> getUsers();
    // 조회(ID)
    public User getUserById(UUID id);
    // 조회(유저ID)
    public User getUserByUserId(String userId);
    // 수정
    public User updateUser(User user, String userName);
    // 삭제
    public User deleteUser(UUID id);
    // 저장
    void saveUsers();
}
