package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.MessageService;


public class JavaApplication {
    public static void main(String[] args) {
        //MessageService msgService = new MessageService(MessageRepository.getMessage)

        System.out.println("=====================================================================");
        System.out.println(" >>> Sprint Posting Channel에 접속하셨습니다.");//User.userlogin(); // 추후 로그인 기능 구현예정
    //        System.out.println(" >>> 원하는 기능을 선택해 주세요...");
    //        System.out.println("=====================================================================\n" +
    //                "| [1] Channel 입장               || [2] 개별 메세지 수정/삭제       |\n" +
    //                "| [3] 개별 메세지 수정/삭제 이력 || [4] Sprint Posting Channel 종료 |\n" +
    //                "=====================================================================");
        MessageService.sendMessage("메세지테스트. 첫번째");
        MessageService.sendMessage("두번째");
        MessageService.sendMessage("세번째");
        MessageService.sendMessage("메시지테스트4");
        MessageService.sendMessage("메시지테스트5");
        MessageService.sendMessage("메시지 마지막");
        MessageService.readAllMessage();

    }


}
