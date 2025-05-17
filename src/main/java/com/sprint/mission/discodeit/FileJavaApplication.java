package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.control.FileChannelControl;
import com.sprint.mission.discodeit.control.FileUserControl;
import com.sprint.mission.discodeit.control.FileMessageControl;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.InputMismatchException;
import java.util.Scanner;

public class FileJavaApplication {
    public static final FileUserService fuserService = new FileUserService();
    public static final FileChannelService fchannelService = new FileChannelService();

    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        User currentUser;
        // 0. intro 메세지
        System.out.println(" ▶ discodeit 메세지 서비스에 연결되었습니다.");
        // 0_1. 초기 사용자 인증
        currentUser = FileUserControl.verifyUser();
        // 1. mainmenu 호출.
        menuMain(currentUser);

            //// <<메뉴 트리>>
           ////  메인메뉴 ┬ 1. 채널입장 (채널상 메세지 관리)   ||  메서드 : control.menuMessageMng()
           ////          │    ├ 1_1 메세지 작성          ok
           ////          │    ├ 1_2 전체 메세지 조회     ok
           ////          │    ├ 1_3 단건 메세지 조회     ok
           ////          │    ├ 1_4 메세지 수정          ok
           ////          │    ├ 1_5 메세지 삭제          ok
           ////          │    └ 1_6 다른 채널에 접속      ok
           ////          │    └ 1_7 현재 사용자 변경      ok
           ////          │    └ 1_8 상위메뉴로            ok
           ////          ├ 2. 채널 관리(신규,수정,삭제)       ||  메서드 : control.menuChannelMng()
           ////          │    ├ 2_1 신규 채널 개설            ok
           ////          │    ├ 2_2 개별 채널 정보 상세조회   ok
           ////          │    ├ 2_3 개별 채널 이름,설명 변경  ok
           ////          │    └ 2_4 개별 채널 삭제            ok
           ////          │    └ 2_5 전체 채널 정보 상세조회   ok
           ////          │    └ 2_6 상위메뉴로                ok
           ////          ├ 3. 사용자 관리(신규,수정,삭제)     ||  메서드 : control.menuUserMng()
           ////          │    ├ 3_1 신규 사용자 추가          ok
           ////          │    ├ 3_2 현재 사용자 변경          ok
           ////          │    ├ 3_3 사용자 삭제               ok
           ////          │    └ 3_4 단일 사용자 상세 조회     ok
           ////          │    └ 3_5 전체 사용자 목록 조회     ok
           ////          │    └ 3_6 현재 사용자 변경          ok
           ////          │    └ 3_7 상위메뉴로                ok
           ////          └ 4. 프로그램 종료
    }

    // ▽▽▽main메서드에서 사용되는 내부 메서드
    public static int scanInt(){    // 메뉴 선택시 int값 입력 필터링 메서드
        int inputNum = -1;
        while (inputNum < 0 ){
            System.out.print(" >> ");
            try {
                inputNum = scanner.nextInt();
                scanner.nextLine(); // scanner 내부 개행문자 제거
            } catch (InputMismatchException e){
                System.out.println("숫자만 입력해 주세요");
                scanner.nextLine(); // scanner 내부 개행문자 제거
            }
        }
        return inputNum;
}

    public static User verifyUser(){ // 사용자명 입력 >> 없는 사용자 : 사용자 생성 / 있는 사용자 : 이름으로 확인, ID연결
        System.out.print(" ▶ 사용자명을 입력해 주세요 : ");
        String username = scanner.nextLine();
        User user = fuserService.findUserByName(username);
        if (user != null) {
            System.out.println("[" + user.getName() + "] 님 환영합니다." + ", ID: " + user.getId());
            return user;
        } else {
            System.out.println("\n ▶ 존재하지 않는 사용자입니다. 신규 사용자로 등록합니다.");
            User newUser = fuserService.createUser(username);
            System.out.println("\n ▶ 환영합니다 [" + newUser.getName() + "] 님, 새로운 ID가 발급되였습니다.         신규 사용자 UUID : " + newUser.getId());
            return newUser;
        }
    }


    public static void menuMain(User nowUser){    // 메인 메뉴 call 메서드. 각 세부 메뉴로 접근하기 위함.
        Channel nowChannel = null;
        while (true) {
            System.out.println(" ▶ 원하는 메뉴를 선택해 주세요.");
            System.out.println("**********************************************\n"
                    + "[1] 채널 입장 및 메세지 관리\n"
                    + "[2] 채널 관리 (신규, 수정, 삭제, 조회)\n"
                    + "[3] 사용자 관리 (신규, 수정, 삭제, 조회)\n"
                    + "[4] 종료\n"
                    + "**********************************************\n");
            int choice = scanInt(); // 숫자 입력 메서드 호출

            switch (choice) {
                case 1:         // 채널 입장 : 메세지 관리 메서드 호출
                    nowChannel = FileChannelControl.joinChannel(nowUser);
                    nowUser = FileMessageControl.menuMessageMng(nowUser,nowChannel);
                    break;
                case 2:
                    FileChannelControl.menuChannelMng(nowUser);
                    break;
                case 3:         // 사용자 관리 : 사용자 관리 메서드 호출
                    nowUser = FileUserControl.menuUserMng(nowUser);
                    break;
                case 4:
                    System.out.println(" ▶ 프로그램을 종료합니다.");
                    break;
                default:
                    System.out.println(" ▶ 잘못된 접근입니다. 다시 입력해 주세요");
            }
            if (choice == 4) {
                break;
            }
        }
    }
}
