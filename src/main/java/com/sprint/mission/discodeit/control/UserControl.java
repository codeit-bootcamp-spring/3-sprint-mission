package com.sprint.mission.discodeit.control;

import com.sprint.mission.discodeit.JavaApplication;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jcf.JCFUserService;

import java.util.InputMismatchException;

public class UserControl extends JavaApplication {
    private static final JCFUserService userService = new JCFUserService();
    // 어플리케이션 최초 실행시 User 신규여부 판별 메서드. 있으면 있는 유저로 로그인 / 없으면 새 유저 생성
    public static User verifyUser(){ // 사용자명 입력 >> 없는 사용자 : 사용자 생성 / 있는 사용자 : 이름으로 확인, ID연결
        System.out.print(" ▶ 사용자명을 입력해 주세요 : ");
        String username = scanner.nextLine();
        User user = userService.findUserByName(username);
        if (user != null) {
            System.out.println("[" + user.getName() + "] 님 환영합니다." + ", ID: " + user.getId());
            return user;
        } else {
            System.out.println("\n ▶ 존재하지 않는 사용자입니다. 신규 사용자로 등록합니다.");
            User newUser = userService.createUser(username);
            System.out.println("\n ▶ 환영합니다 [" + newUser.getName() + "] 님, 새로운 ID가 발급되였습니다.\n 신규 사용자 UUID : " + newUser.getId());
            return newUser;
        }
    }
    // 1_3 사용자 관리 세부메뉴 메서드
    public static void menuUserMng(User currentUser){       // 1_4 사용자 관리 내부메뉴 메서드
        while (true) {
            System.out.println(" *******************************************************\n"
                    + " || 사용자 관리 메뉴입니다. 원하는 기능을 선택하세요. ||\n"
                    + " ||      1 > 신규 사용자 생성                         ||\n"
                    + " ||      2 > 현재 사용자 이름 변경                    ||\n"
                    + " ||      3 > 사용자 삭제                              ||\n"
                    + " ||      4 > 단일 사용자 상세조회                     ||\n"
                    + " ||      5 > 전체 사용자 상세조회                     ||\n"
                    + " ||      6 > 다른 사용자로 진행                       ||\n"
                    + " ||      7 > 상위 메뉴로 돌아가기                     ||\n"
                    + " *******************************************************\n");
            System.out.print(" >> ");
            int choice = -1;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // scanner 내부 개행문자 제거
            } catch (InputMismatchException e){
                System.out.println("숫자만 입력해 주세요");
                scanner.nextLine(); // scanner 내부 개행문자 제거
            }

            switch (choice) {
                case 1:                 // 1_4_1 신규 사용자 생성 메뉴
                    System.out.println(" ▶ 새로운 사용자를 생성합니다.");
                    while (true) {
                        System.out.print(" ▶ 원하는 사용자명을 입력해 주세요 : ");
                        String username = scanner.nextLine();
                        if (username.length() == 0) {   // 입력값이 없는경우 상위 메뉴로 이동
                            System.out.println(" ▶ 입력값이 없습니다. 사용자 생성을 취소합니다.\n");
                            break;}
                        User user = userService.findUserByName(username);
                        if (user == null) {                 // 중복 유저명 체크하여 중복유저가 없는경우 사용자 생성
                            User newUser = userService.createUser(username);
                            System.out.println(" ▶ 사용자 생성에 성공했습니다.");
                            System.out.println(" ▶ 사용자명 : " + newUser.getName() + "       SSID : " + newUser.getId() + "\n");

                            // 새로 생성한 사용자로 currentUser 변경여부 체크
                            System.out.println(" ▶ 새로운 사용자로 로그인하시겠습니까?     Y/N");
                            String newUserChange = scanner.nextLine();
                            if (newUserChange.equals("y") || newUserChange.equals("Y")) {
                                currentUser = newUser;
                                System.out.println("\n ▶ [" + currentUser.getName() + "] 사용자로 로그인 했습니다.");
                            }

                            System.out.println(" ▶ 기존 사용자 [" + currentUser.getName() + "]로 계속합니다.");
                            break;
                        } else {    // user.name 중복값 확인시 while문 재시작
                            System.out.println(" ▶ 이미 존재하는 사용자명입니다. 다른 이름을 입력해 주세요.\n");
                        }
                    }
                    break;

                case 2:
                    while (true){                    // 1_4_2 사용자 이름 변경 메뉴
                        System.out.println("\n ▶  사용자 이름을 변경합니다.   * 현재 사용자 이름 : [" + currentUser.getName() + "]");
                        System.out.println(" ▶ 변경할 사용자 이름을 입력해 주세요");
                        System.out.print(" >> ");
                        String newName = scanner.nextLine();
                        if(newName.length() ==0) {// 사용자 이름 입력값이 없는경우 상위메뉴로 이동
                            System.out.println(" ▶ 새 이름을 입력하지 않았습니다. 사용자 이름 변경을 취소합니다.");
                            break;}
                        User user = userService.findUserByName(newName);
                        if(user != null){             // 중복값 존재시 재입력
                            System.out.print(" ▶ 이미 존재하는 이름입니다. 다시 입력해 주세요.\n >>> ");
                        } else{                       // 중복값 미확인시 유저명 변경
                            userService.updateUserName(currentUser, newName);
                            break;
                        }
                    }
                    break;
                case 3:                    // 1_4_3 사용자 삭제 메뉴
                    System.out.println(" ▶ 삭제할 사용자의 이름을 입력해 주세요");
                    System.out.println(" ▶ 현재 등록된 사용자는 아래와 같습니다.");
                    userService.printAllUsers();
                    System.out.print(" >> ");
                    String deleteUser = scanner.nextLine();
                    if ((deleteUser.length() == 0) || (userService.findUserByName(deleteUser) == null)) {
                        System.out.println(" ▶ 존재하지 않는 사용자이거나 잘못된 입력입니다. 이전 메뉴로 돌아갑니다.");
                    } else if (deleteUser.equals(currentUser.getName())){
                        System.out.println(" ▶ 현재 로그인된 사용자는 삭제할 수 없습니다! 이전 메뉴로 돌아갑니다.");
                        break;
                    } else {
                        System.out.println(" ▶ 입력하신 사용자는 아래와 같습니다.");
                        User userToDel = userService.findUserByName(deleteUser);
                        System.out.println(userToDel);
                        System.out.println(" ▶ 정말로 삭제할까요? 삭제를 원하시면 [삭제]라고 입력해 주세요");
                        System.out.print(" >> ");
                        String deleteConfirm = scanner.nextLine();
                        if (deleteConfirm.length() == 0) {
                            System.out.println(" ▶ 잘못된 입력입니다. 이전 메뉴로 돌아갑니다.");
                            break;
                        }else if(deleteConfirm.equals("삭제")) {
                            userService.deleteUser(deleteUser);
                            System.out.println("[" + deleteUser + "] 사용자가 삭제되었습니다.");
                        }else{
                            System.out.println(" ▶ 잘못된 입력입니다. 사용자 삭제를 취소합니다.");
                        }
                    }
                    break;
                case 4:                    // 1_4_4 단일 사용자 정보 출력 메뉴
                    System.out.print(" ▶ 조회할 사용자의 이름을 입력해 주세요.\n >> ");
                    String oneUser = scanner.nextLine();
                    if ((oneUser.length() == 0) || (userService.findUserByName(oneUser) == null)) {
                        System.out.println(" ▶ 존재하지 않는 사용자이거나 잘못된 입력입니다. 이전 메뉴로 돌아갑니다.");
                    } else {
                        System.out.println(" ▶ 입력하신 사용자는 아래와 같습니다.");
                        User userToRead = userService.findUserByName(oneUser);
                        System.out.println(userToRead);
                    }
                    break;
                case 5:                    // 1_4_5 모든 사용자 정보 출력 메뉴
                    System.out.println(" ▶ 모든 사용자 정보를 출력합니다.");
                    userService.printAllUsers();
                    break;
                case 6:                    // 1_4_6 다른 사용자로 진행
                    System.out.print(" ▶ 어떤 사용자로 로그인할까요?");
                    String username = scanner.nextLine();
                    User user = userService.findUserByName(username);
                    if (user != null) {
                        System.out.println("[" + user.getName() + "] 사용자로 진행합니다." + ", ID: " + user.getId());
                        currentUser =  user;
                    } else {
                        System.out.println("\n ▶ 존재하지 않는 사용자입니다. 기존 사용자[" + currentUser.getName() + "]로 진행합니다.\n 현재 사용가능한 사용자는 아래와 같습니다.");
                        userService.getUserslist().forEach(u -> System.out.print(u.getName()+ " , "));
                        System.out.println();
                    }
                    break;
                case 7:                    // 1_4_7 상위메뉴 복귀
                    break;
                default:                   // 1_4 세부메뉴 입력예외처리
                    System.out.println(" ▶ 잘못된 접근입니다. 다시 입력해 주세요");
            }
            if (choice == 7) {break;}
        }
    }   // 1_3 사용자 관리 세부메뉴 메서드

}
