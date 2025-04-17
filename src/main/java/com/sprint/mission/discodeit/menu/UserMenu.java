package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.Scanner;
import java.util.UUID;

public class UserMenu {
    public static void manageUsers(Scanner scanner, JCFUserService userService) {
        while (true) {
            System.out.println("\n===== USER MENU =====");
            System.out.println("1. 사용자 등록");
            System.out.println("2. 사용자 단건 조회");
            System.out.println("3. 사용자 다건 조회");
            System.out.println("4. 사용자 수정");
            System.out.println("5. 사용자 삭제");
            System.out.println("0. 초기 화면으로");
            System.out.print("번호를 입력하세요: ");
            String select = scanner.nextLine();

            try {
                switch (select) {
                    case "1":
                        System.out.print("사용자 이름 입력: ");
                        String name = scanner.nextLine();
                        User user = new User(name);
                        userService.createUser(user);
                        System.out.println("생성된 사용자 ID: " + user.getId());
                        break;
                    case "2":
                        System.out.print("조회할 사용자 ID 입력: ");
                        UUID id = UUID.fromString(scanner.nextLine());
                        User find = userService.getUser(id);
                        System.out.println(find != null ? find : "사용자를 찾을 수 없습니다.");
                        break;
                    case "3":
                        userService.getAllUsers().forEach(System.out::println);
                        break;
                    case "4":
                        System.out.print("수정할 사용자 ID 입력: ");
                        UUID updateId = UUID.fromString(scanner.nextLine());
                        System.out.print("새 이름 입력: ");
                        String newName = scanner.nextLine();
                        userService.updateUser(updateId, newName);
                        break;
                    case "5":
                        System.out.print("삭제할 사용자 ID 입력: ");
                        UUID delId = UUID.fromString(scanner.nextLine());
                        userService.deleteUser(delId);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("올바른 번호를 선택하세요.");
                }
            } catch (Exception e) {
                System.out.println("[오류] 사용자 메뉴 처리 중 문제가 발생했습니다: " + e.getMessage());
            }
        }
    }
}