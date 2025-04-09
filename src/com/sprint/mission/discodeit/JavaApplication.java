package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jcf.JCFMessageService;
import com.sprint.mission.discodeit.jcf.JCFUserService;

import java.util.InputMismatchException;
import java.util.Scanner;

public class JavaApplication {
    private static final JCFUserService userService = new JCFUserService();
    private static final JCFMessageService messageService = new JCFMessageService();
    static final Scanner scanner = new Scanner(System.in);
    private static User currentUser;

    public static void main(String[] args) {
        // 0. 초기 사용자 인증
        currentUser = verifyUser();
        // 0_1. intro 메세지
        System.out.println(" ▶ discodeit 메세지 서비스에 연결되었습니다.");
        // 1. mainmenu 호출
        menuMain();
            //// <<메뉴 트리>>
           ////  메인메뉴 ┬ 1. 채널입장 (채널상 메세지 관리)   ||  메서드 : menuMessageMng()
           ////          │    ├ 1_1 메세지 작성
           ////          │    ├ 1_2 전체 메세지 조회
           ////          │    ├ 1_3 메세지 수정
           ////          │    ├ 1_4 메세지 삭제
           ////          │    └ 1_5 현재 사용자 변경
           ////          │    └ 1_6 상위메뉴로
           ////          ├ 2. 채널 관리(신규,수정,삭제)       ||  메서드 : menuChannelMng()
           ////          │    ├ 2_1 신규 채널 개설
           ////          │    ├ 2_2 채널명 수정
           ////          │    ├ 2_3 채널 삭제
           ////          │    └ 2_4 전체 채널 목록 조회
           ////          │    └ 2_5 상위메뉴로
           ////          ├ 3. 사용자 관리(신규,수정,삭제)     ||  메서드 : menuUserMng()
           ////          │    ├ 3_1 신규 사용자 추가
           ////          │    ├ 3_2 현재 사용자 변경
           ////          │    └ 3_3 전체 사용자 목록 조회
           ////          │    └ 3_4 전체 사용자 목록 조회
           ////          │    └ 3_5 상위메뉴로
           ////          └ 5. 프로그램 종료

        // 이름으로 찾기
//        userService.findUserByName("이유정").ifPresent(u ->
//                System.out.println("Found by name: " + u.getName() + ", ID: " + u.getId())
//        );
//        // ID로 삭제
//        boolean deleted = userService.deleteUserById(user1.getId());
//        System.out.println("Deleted Alice by ID: " + deleted);
//
//        // 남은 사용자 출력
//        System.out.println("Remaining Users: ");
//        userService.getAllUsers().forEach(u -> System.out.println(u.getName()));

    }
    private static int scanInt(){
        System.out.print(" >> ");
        int inputNum = -1;
        try {
            inputNum = scanner.nextInt();
            scanner.nextLine(); // scanner 내부 개행문자 제거
        } catch (InputMismatchException e){
            System.out.println("숫자만 입력해 주세요");
            scanner.nextLine(); // scanner 내부 개행문자 제거
        }
        return inputNum;
}
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
    public static void menuMain(){
        while (true) {
            System.out.println(" ▶ 원하는 메뉴를 선택해 주세요.");
            System.out.println("**********************************************\n"
                    + "[1] 채널 입장\n"
                    + "[2] 채널 관리 (신규, 수정, 삭제, 조회)\n"
                    + "[3] 사용자 관리 (신규, 수정, 삭제, 조회)\n"
                    + "[4] 종료\n"
                    + "**********************************************\n");
            int choice = scanInt(); // 숫자 입력 메서드 호출

            switch (choice) {
                case 1:         // 채널 입장 : 메세지 관리 메서드 호출
                    menuMessageMng(currentUser);
                    break;
                case 2:
                    System.out.println(" ▶ 2번 메뉴는 아직 미지원입니다.\n ");
                    //            menuSetting();
                    break;
                case 3:         // 사용자 관리 : 사용자 관리 메서드 호출
                    menuUserMng(currentUser);
                    break;
                case 4:
                    System.out.println(" ▶ 프로그램을 종료합니다.");
                    break;
                default:
                    System.out.println(" ▶ 잘못된 접근입니다. 다시 입력해 주세요");
            }
            if (choice == 4) {break;}
        }

    }
    public static void menuMessageMng(User currentUser){       // 1 채널 조작 메서드

        while (true) {
            System.out.println(" *******************************************************\n"
                    + " || 채널에 입장했습니다.    원하는 기능을 선택하세요. ||\n"
                    + " ||      1 > 신규 메세지 작성                         ||\n"
                    + " ||      2 > 전체 메세지 조회                         ||\n"
                    + " ||      3 > 메세지 수정                              ||\n"
                    + " ||      4 > 메세지 삭제                              ||\n"
                    + " ||      5 > 현재 사용자 변경                         ||\n"
                    + " ||      6 > 상위 메뉴로 돌아가기                     ||\n"
                    + " *******************************************************\n");
            int choice = scanInt(); // 숫자 입력 메서드 호출
            int inputMsgNum = -1;
            Message currentMsg = null;
            switch (choice) {
                case 1:                 // 1_1 메세지 작성
                        while (true){
                            System.out.println(" ▶ 새로운 메세지를 등록합니다.\n ");
                        boolean breakpoint= messageService.uploadMsg(currentUser);
                        if (breakpoint != true){
                            break;
                        }
                    }
                    break;

                case 2:               //1_2 전체 메세지 조회
                    System.out.println(" ▶ 채널에 등록된 모든 메세지를 조회합니다.\n ");
                    messageService.printAllMessages();
                    break;
                case 3:               // 1_3 메세지 수정
                    System.out.println(" ▶ 현재 등록된 메세지는 아래와 같습니다.");
                    messageService.printAllMessages();
                    System.out.println(" ▶ 수정할 메세지의 번호를 입력해 주세요");
                    inputMsgNum = scanInt(); // 숫자 입력 메서드 호출
                    currentMsg = messageService.findMessageByNum(inputMsgNum);
                    System.out.println(" ▶ [" + inputMsgNum + "]번 메세지는 아래와 같습니다.");
                        System.out.println(currentMsg.getTextMsg());

                    System.out.print("수정할 새로운 메세지를 입력해 주세요.\n >> ");
                        String newMessage = scanner.nextLine();
                        messageService.updateMsg(currentMsg,newMessage);
                        System.out.println("기존 내용을 [" + newMessage + "] 로 수정하였습니다.");
                    break;
                case 4:               // 1_4 메세지 삭제
                    System.out.println(" ▶ 현재 등록된 메세지는 아래와 같습니다.");
                    messageService.printAllMessages();
                    System.out.println(" ▶ 삭제할 메세지의 번호를 입력해 주세요");
                    inputMsgNum = scanInt(); // 숫자 입력 메서드 호출
                    currentMsg = messageService.findMessageByNum(inputMsgNum);
                    System.out.println(" ▶ [" + inputMsgNum + "]번 메세지는 아래와 같습니다.");
                    System.out.println(currentMsg.getTextMsg());

                    System.out.println(" ▶ 정말로 삭제할까요? 삭제를 원하시면 [삭제]라고 입력해 주세요.\n >> ");
                    String deleteConfirm = scanner.nextLine();
                    if (deleteConfirm.length() == 0) {
                        System.out.println(" ▶ 잘못된 입력입니다. 이전 메뉴로 돌아갑니다.");
                        break;
                    }else if(deleteConfirm.equals("삭제")) {
                        messageService.deleteMessage(currentMsg);
                        System.out.println("메세지가 삭제되었습니다.");
                    }else{
                        System.out.println(" ▶ 잘못된 입력입니다. 사용자 삭제를 취소합니다.");
                    }
                    break;
                case 5:               // 1_5 현재 사용자 변경
                    System.out.print(" ▶ 어떤 사용자로 로그인할까요?");
                    String username = scanner.nextLine();
                    User user = userService.findUserByName(username);
                    if (user != null) {
                        System.out.println("[" + user.getName() + "] 사용자로 진행합니다." + ", ID: " + user.getId());
                        currentUser =  user;
                    } else {
                        System.out.println("\n ▶ 존재하지 않는 사용자입니다. 기존 사용자[" + currentUser.getName() + "]로 진행합니다.\n 현재 사용가능한 사용자는 아래와 같습니다.");
                        userService.getUserslist().forEach(u -> System.out.print(u.getName()+ " , "));
                        System.out.println("");
                    }
                    break;
                case 6:               // 1_6 상위메뉴로 이동
                    break;
                default:              // 세부메뉴 입력예외처리
                    System.out.println(" ▶ 잘못된 접근입니다. 다시 입력해 주세요");
            }
            if (choice == 6) {break;}
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
                    + " ||      4 > 전체 사용자 상세조회                     ||\n"
                    + " ||      5 > 다른 사용자로 진행                       ||\n"
                    + " ||      6 > 상위 메뉴로 돌아가기                     ||\n"
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
                    System.out.println(" ▶ 삭제할 사용자의 ID나 이름을 입력해 주세요");
                    System.out.println(" ▶ 현재 등록된 사용자는 아래와 같습니다.");
                    userService.printAllUsers();
                    System.out.print(" >> ");
                    String deleteUser = scanner.nextLine();
                    if (deleteUser.length() == 0) {
                        System.out.println(" ▶ 잘못된 입력입니다. 이전 메뉴로 돌아갑니다.");
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
                case 4:                    // 1_4_4 모든 사용자 정보 출력 메뉴
                    System.out.println(" ▶ 모든 사용자 정보를 출력합니다.");
                    userService.printAllUsers();
                    break;
                case 5:                    // 1_4_5 다른 사용자로 진행
                    System.out.print(" ▶ 어떤 사용자로 로그인할까요?");
                    String username = scanner.nextLine();
                    User user = userService.findUserByName(username);
                    if (user != null) {
                        System.out.println("[" + user.getName() + "] 사용자로 진행합니다." + ", ID: " + user.getId());
                        currentUser =  user;
                    } else {
                        System.out.println("\n ▶ 존재하지 않는 사용자입니다. 기존 사용자[" + currentUser.getName() + "]로 진행합니다.\n 현재 사용가능한 사용자는 아래와 같습니다.");
                        userService.getUserslist().forEach(u -> System.out.print(u.getName()+ " , "));
                        System.out.println("");
                    }
                    break;
                case 6:                    // 1_4_6 상위메뉴 복귀
                    break;
                default:                   // 1_4 세부메뉴 입력예외처리
                    System.out.println(" ▶ 잘못된 접근입니다. 다시 입력해 주세요");
            }
            if (choice == 6) {break;}
        }


    }   // 1_4 사용자 관리 세부메뉴 메서드

}



