package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Scanner;

public class UserMenu {
    private final Scanner scanner;
    private final UserService userService;
    private final List<User> users;
    private User loginUser;

    public UserMenu(Scanner scanner, UserService userService, List<User> users) {
        this.scanner = scanner;
        this.userService = userService;
        this.users = users;
    }

    public void loginUser() {
        System.out.println("안녕하세요. DisCodeit에 오신 걸 환영합니다.");
        System.out.println("사용자의 이름을 입력해 주세요.");
        String name = scanner.nextLine();
        loginUser = new User(name);
        users.add(loginUser);

        System.out.println("로그인 하실 프로필 번호를 입력해 주세요.");
        userService.outputAllUsersInfo();
        int loginNumber = Integer.parseInt(scanner.nextLine());
        userService.login(loginNumber, users);
        loginUser = userService.changeUser(loginNumber);

        System.out.println(loginUser.getUsername() + "님, 반갑습니다.");
    }

    public void run() {
        if (loginUser == null) {
            System.out.println("사용자가 로그인되지 않았습니다.");
            return;
        }
        while (true) {
            System.out.println(
                    "1. 새로운 프로필 추가\t2. 모든 프로필 정보 출력\t3. 프로필 정보 출력\t4. 프로필 이름 수정\t5. 프로필 정보 삭제\t6. 프로필 변경\t7. 이전 메뉴");
            int n = Integer.parseInt(scanner.nextLine());

            switch (n) {
                case 1 -> {
                    System.out.println("새로운 프로필 이름을 입력해 주세요.");
                    String newUser = scanner.nextLine();
                    userService.createNewUserNames(loginUser.getUsername(), newUser);
                }
                case 2 -> userService.outputAllUsersInfo();
                case 3 -> userService.outputOneUserInfo(loginUser.getUsername());
                case 4 -> {
                    System.out.println("현재 사용자 이름: " + loginUser.getUsername() + ". 변경할 이름을 입력해 주세요.");
                    String updateUserName = scanner.nextLine();
                    userService.updateUserName(loginUser, updateUserName);
                }
                case 5 -> {
                    userService.outputAllUsersInfo();
                    System.out.println("삭제할 사용자의 번호를 입력해 주세요.");
                    int num = Integer.parseInt(scanner.nextLine());
                    System.out.println(loginUser.getUsername() + " 정보를 삭제합니다.");
                    userService.deleteUserName(num);
                }
                case 6 -> {
                    System.out.println("변경할 프로필 번호를 입력해 주세요.");
                    userService.outputAllUsersInfo();
                    int loginNumber = Integer.parseInt(scanner.nextLine());
                    userService.login(loginNumber, users);
                    loginUser = userService.changeUser(loginNumber);
                }
                case 7 -> {
                    return;
                }
                default -> System.out.println("올바른 번호를 입력해 주세요.");
            }
        }
    }
}