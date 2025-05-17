package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class JCFUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();
    // 테스트용 초기 더미 데이터 입력
    public JCFUserRepository() {
        users.add(new User("희동이" , 1744036538250L , 1744036538250L));
        users.add(new User("고길동" , 1744036738250L , 1744071238250L));
        users.add(new User("도우너" , 1744036938250L , 1744036538250L));
    }
    // 유저 목록 반환
    public List<User> getUserslist() {
        return users;
    }

    public void saveUsersList(){}
}