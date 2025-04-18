package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Scanner;

public class BasicUserService implements UserService {
    private final UserRepository userRepo;
    private static Scanner sc = new Scanner(System.in);

    public BasicUserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User inputUserName() {
        System.out.println("안녕하세요. DisCodeit에 오신 걸 환영합니다.");
        System.out.println("사용자의 이름을 입력해 주세요.");
        String name = sc.nextLine();
        User newUser = new User(name);
        userRepo.saveUser(newUser);
        return newUser;
    }


    @Override
    public void createNewUserNames(String oldName, String newName) {
        if (oldName.equals(newName.trim())) {
            System.out.println("동일한 프로필은 생성할 수 없습니다.");
        } else {
            User u = new User(newName);
            userRepo.saveUser(u);
        }
    }

    @Override
    public void outputAllUsersInfo() {
        for (User u : userRepo.findAllUser()) {
            System.out.println(u);
        }
    }

    @Override
    public void outputOneUserInfo(String name) {
        userRepo.findAllUser().stream()
                .filter(u -> u.getUsername().equals(name))
                .forEach(System.out::println);
    }

    @Override
    public void updateUserName(User user, String newName) {
        if (user.getUsername().equals(newName.trim())) {
            System.out.println("프로필 이름은 중복 될 수 없습니다.");
        } else {
            user.setUsername(newName);
            userRepo.updateUser(user);
        }
    }

    @Override
    public void deleteUserName(int userNumber) {
        userRepo.deleteUser(userNumber);
    }

    @Override
    public User changeUser(int userNumber) {
        return userRepo.findUser(userNumber);
    }

    @Override
    public void login(int loginNumber, java.util.List<User> ignore) {
        if (userRepo.findUser(loginNumber) == null) {
            throw new RuntimeException("해당 번호의 유저가 존재하지 않습니다");
        }
    }
}