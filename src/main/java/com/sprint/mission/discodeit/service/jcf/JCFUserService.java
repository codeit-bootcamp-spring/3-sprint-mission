package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserInterface;
import java.util.List;
import java.util.Scanner;

public class JCFUserService implements UserInterface {

    private final List<User> users;

    public JCFUserService(List<User> users) {
        this.users = users;
    }


    public User inputUserName() {
        Scanner sc = new Scanner(System.in);
        System.out.println("안녕하세요. DisCodeit에 오신 걸 환영합니다. 사용자의 이름을 입력 해 주세요.");
        String userName = sc.nextLine();
        return new User(userName);
    }

    @Override
    public void createNewUserNames(String oldName, String newName) {
        if (oldName.equals(newName.trim())) {
            System.out.println("동일한 프로필은 생성할 수 없습니다.");
        } else {
            users.add(new User(newName));
        }
    }

    public void outputAllUsersInfo() {
        for (User user : users) {
            System.out.println(user);
        }
    }

    public void outputOneUserInfo(String name) {
        List<User> userName = users
                .stream()
                .filter(e -> e.getUsername().equals(name))
                .toList();
        System.out.println(userName);
    }

    public void updateUserName(String oldName, String newName) {
        if (oldName.equals(newName.trim())) {
            System.out.println("프로필 이름은 중복 될 수 없습니다.");
        } else {
            users.stream()
                    .filter(user -> user.getUsername().equals(oldName))
                    .findFirst()
                    .ifPresent(user -> user.updateUserName(newName));
        }

    }

    public void deleteUserName(int userNumber) {
        users.stream()
                .filter(user -> user.getNumber() == userNumber)
                .findFirst()
                .ifPresent(user -> users.remove(user));
    }

    public User changeUser(int userNumber) {
        return users.stream().filter(user1 -> user1.getNumber() == userNumber).findFirst().orElse(null);
    }


}
