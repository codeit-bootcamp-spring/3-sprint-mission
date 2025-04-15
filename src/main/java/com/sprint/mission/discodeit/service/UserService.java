package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

//Q.data는 map 타입인데 return은 list 타입으로 하는게좋은가?
public interface UserService {

    public void create(User user);

    public User read(UUID id);

    public List<User> read(String name);

    public List<User> readAll();

    public User update(UUID id, String name);

    public User update(UUID id, int age);

    public User update(UUID id, String name, int age);

    public boolean delete(UUID id);

}
