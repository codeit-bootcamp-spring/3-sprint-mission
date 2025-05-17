package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileUserService implements UserService {
    UserRepository userRepository = new FileUserRepository();
    private final List<User> users = userRepository.getUserslist();

    @Override       // 유저 생성 메서드 createUser()
    public User createUser(String name) {
        long now = System.currentTimeMillis();
        User user = new User(name, now, now);
        users.add(user);
        userRepository.saveUsersList();
        System.out.println("사용자의 이름을 [" + user.getName() + "] 으로 생성하였습니다.   |   " + user.getCreatedAt());
        return user;
    }

    @Override       //유저 이름 변경 메서드 updateUserName()
    public void updateUserName(User user, String newName) {
        String oldName = user.getName();
        user.setName(newName);
        user.setUpdatedAt(System.currentTimeMillis());
        userRepository.saveUsersList();
        System.out.println("[" + oldName + "] 사용자의 이름을 [" + user.getName() + "] 으로 변경하였습니다. |   " + user.getUpdatedAt());
    }

    @Override
    public void deleteUser(String name) {
        users.remove(findUserByName(name));
        userRepository.saveUsersList();
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

    // 모든 사용자 출력
    public void showAllUsers(){
        System.out.println("  □ □ □ 전체 사용자 목록 □ □ □ \n  사용자이름   |   사용자생성시간   | 사용자정보 수정시간 |   사용자UUID");
        users.forEach(u -> System.out.println("  " + u.getName() + "       |   " + u.getCreatedAt() + "   |   " + u.getUpdatedAt() + "    |   " + u.getId()));
        System.out.println();
    }
    public List<User> getUserslist() {
        return users;
    }
}