package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final UserRepository userRepository = new JCFUserRepository();
    private final List<User> users = userRepository.getUserslist();

    @Override       // 유저 생성 메서드 createUser()
    public User createUser(String name) {
        long now = System.currentTimeMillis();
        User user = new User(name, now, now);
        users.add(user);
        System.out.println("사용자의 이름을 [" + user.getName() + "] 으로 생성하였습니다.   |   " + user.getCreatedAt());
        return user;
    }

    @Override       //유저 이름 변경 메서드 updateUserName()
    public void updateUserName(User user, String newName) {
        String oldName = user.getName();
        user.setName(newName);
        user.setUpdatedAt(System.currentTimeMillis());
        System.out.println("[" + oldName + "] 사용자의 이름을 [" + user.getName() + "] 으로 변경하였습니다. |   " + user.getUpdatedAt());
    }

    @Override
    public void deleteUser(String name) {
        users.removeIf(user -> user.getName().equals(name));
    }

    @Override
    public User findUserByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user; // 일치하는 name 발견시 user 리턴
            }
        }
        return null; // 없으면 null 리턴
    }

    // 유틸 메서드: 모든 사용자 출력
    public void showAllUsers(){
        System.out.println("  □ □ □ 전체 사용자 목록 □ □ □ \n  사용자이름   |   사용자생성시간   | 사용자정보 수정시간 |   사용자UUID");
        users.forEach(u -> System.out.println("  " + u.getName() + "       |   " + u.getCreatedAt() + "   |   " + u.getUpdatedAt() + "    |   " + u.getId()));
        System.out.println();
    }
    public List<User> getUserslist() {
        return users;
    }
}