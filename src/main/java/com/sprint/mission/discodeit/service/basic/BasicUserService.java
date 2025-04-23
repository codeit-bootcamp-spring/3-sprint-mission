package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Scanner;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepo;
    private static Scanner sc = new Scanner(System.in);

    public BasicUserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User registerUser() {
        System.out.println("안녕하세요. DisCodeit에 오신 걸 환영합니다.");
        System.out.println("사용자의 이름을 입력해 주세요.");

        while (true) {
            String name = sc.nextLine();
            User newUser;
            boolean isDuplicate = userRepo.findAllUser().stream()
                    .anyMatch(u -> u.getUsername().equals(name.trim()));

            if (isDuplicate) {
                System.out.println("이미 존재하는 이름입니다. 다시 입력해주세요.");
            } else {
                newUser = new User(name.trim());
                userRepo.saveUser(newUser);
                System.out.println("새로운 프로필이 생성되었습니다.");
                return newUser;
            }
        }

    }


    @Override
    public void createNewUserNames(String existingName, String newName) {
        String trimmedNewName = newName.trim();

        boolean isDuplicate = userRepo.findAllUser().stream()
                .anyMatch(u -> u.getUsername().equals(trimmedNewName));

        if (isDuplicate) {
            System.out.println("이미 존재하는 이름입니다.");
        } else {
            User newUser = new User(trimmedNewName);
            userRepo.saveUser(newUser);
            System.out.println("새로운 프로필이 생성되었습니다.");
        }
    }

    @Override
    public void outputAllUsersInfo() {
        for (User u : userRepo.findAllUser()) {
            System.out.println(u);
        }
    }

    @Override
    public void outputOneUserInfo(UUID uuid) {
        userRepo.findAllUser().stream()
                .filter(u -> u.getId().equals(uuid))
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