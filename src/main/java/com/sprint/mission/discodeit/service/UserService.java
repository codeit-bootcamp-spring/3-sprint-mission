package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

public interface UserService {

    public User create(String name, int age, String email, String password);

    public User find(UUID userId);

    public List<User> find(String name);

    public List<User> findAll();

    public User update(UUID userId, String newName, int newAge, String newEmail, String newPassword);

    public void delete(UUID userId);

    // XXX : 불가능. userservice는 channel service에 의존하고 있지 않으므로
//    public List<User> findAttendeesByChannel(UUID channelId);
}
