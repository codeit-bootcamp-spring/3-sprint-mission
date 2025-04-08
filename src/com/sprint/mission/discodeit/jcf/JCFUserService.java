package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFUserService implements UserService {
    private final List<User> users = new ArrayList<>();
    // 테스트용 초기 더미 데이터 입력
    public JCFUserService() {
        users.add(new User("희동이" , 1744036538250L , 1744036538250L));
        users.add(new User("고길동" , 1744036738250L , 1744071238250L));
        users.add(new User("도우너" , 1744036938250L , 1744036538250L));
    }

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
    public User findUserById(UUID id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user; // 일치하는 ID 발견시 user 리턴
            }
        }
        return null; // 없으면 null 리턴
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

    @Override
    public boolean deleteUser(UUID id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    @Override
    public boolean deleteUser(String name) {

        return users.removeIf(user -> user.getName().equals(name));
    }



    // 유틸 메서드: 모든 사용자 출력
    public void printAllUsers(){
        System.out.println("  □ □ □ 전체 사용자 목록 □ □ □ \n  사용자이름   |   사용자생성시간   | 사용자정보 수정시간 |   사용자UUID");
        users.forEach(u -> System.out.println("  " + u.getName() + "       |   " + u.getCreatedAt() + "   |   " + u.getUpdatedAt() + "    |   " + u.getId()));
        System.out.println("");
    }
    public List<User> getUserslist() {
        return new ArrayList<>(users);
    }

}