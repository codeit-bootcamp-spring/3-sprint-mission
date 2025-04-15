package com.sprint.mission.discodeit.control;

import com.sprint.mission.discodeit.JavaApplication;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public class MessageControl extends JavaApplication {
    public static void menuMessageMng(User currentUser, Channel currentChannel){       // 1 메세지 관리 메서드

        while (true) {
            System.out.println(" *******************************************************\n"
                    + " ||   ["+currentChannel.getChannelName()+"] 채널에 입장했습니다.\n"
                    + " ||   원하는 기능을 선택하세요.                       ||\n"
                    + " ||      1 > 신규 메세지 작성                         ||\n"
                    + " ||      2 > 전체 메세지 조회                         ||\n"
                    + " ||      3 > 단건 메세지 조회                         ||\n"
                    + " ||      4 > 메세지 수정                              ||\n"
                    + " ||      5 > 메세지 삭제                              ||\n"
                    + " ||      6 > 다른 채널에 접속                         ||\n"
                    + " ||      7 > 현재 사용자 변경                         ||\n"
                    + " ||      8 > 상위 메뉴로 돌아가기                     ||\n"
                    + " *******************************************************\n");
            int choice = scanInt(); // 숫자 입력 필터링 메서드 호출
            int inputMsgNum;
            Message currentMsg;
            UUID channelId = currentChannel.getId();
            List<Message> messages = messageService.getMessagesList(channelId);
            System.out.println(messages.size());
            switch (choice) {
                case 1:                 // 1_1 메세지 작성
                    System.out.print(" ▶ 새로운 대화를 시작합니다. 대화를 종료하려면 빈 값을 입력해주세요.\n");
                    currentChannel.getId();
                    while (true){                   //대화메세지를 연속으로 입력받도록 루프
                        int msgDisplaySize;
                        if((messages.size()) < 5){
                            msgDisplaySize=0;
                            System.out.println("5이하의메세지크기");
                        } else {
                            System.out.println("5이상의메세지크기");
                            msgDisplaySize = messages.size() - 5
                            ;
                        }
                        System.out.println("번호 | 사용자명     : 메세지(생성일/수정일)");
                        for (int i = msgDisplaySize ; i < messages.size(); i++) {
                            messageService.printOneMessage(channelId,i+1);
                        }

                        System.out.print(" >> ");
                        String txtMsg = scanner.nextLine();
                        if (txtMsg.length()!=0) {

                            messageService.uploadMsg(channelId, currentUser.getName(), txtMsg);
                        } else {
                            System.out.println(" ▶ 메세지 입력을 종료합니다");
                            break;
                        }
                    }
                    break;

                case 2:               //1_2 전체 메세지 조회
                    System.out.println(" ▶ 채널에 등록된 모든 메세지를 조회합니다.\n ");
                    messageService.printAllMessages(channelId);
                    break;
                case 3:               //1_3 단건 메세지 조회
                    System.out.println(" ▶ 조회할 메세지의 번호를 입력해 주세요");
                    inputMsgNum = scanInt(); // 숫자 입력 메서드 호출
                    if(inputMsgNum <= messages.size()) {
                        System.out.println(" ▶ [" + inputMsgNum + "]번 메세지는 아래와 같습니다.");
                        System.out.println("번호 | 사용자 : 메세지 ( 생성시각 / 수정시각 )");
                        messageService.printOneMessage(channelId,inputMsgNum);
                        break;
                    }
                    System.out.println(" ▶ 잘못된 입력입니다. 상위메뉴로 돌아갑니다.");

                    break;
                case 4:               // 1_4 메세지 수정
                    System.out.println(" ▶ 현재 등록된 메세지는 아래와 같습니다.");
                    messageService.printAllMessages(channelId);
                    System.out.println(" ▶ 수정할 메세지의 번호를 입력해 주세요");
                    inputMsgNum = scanInt(); // 숫자 입력 메서드 호출
                    if(inputMsgNum <= messages.size()) {
                        currentMsg = messageService.findMessageByNum(channelId,inputMsgNum);
                        System.out.println(" ▶ [" + inputMsgNum + "]번 메세지는 아래와 같습니다.");
                        System.out.println(currentMsg.getTextMsg());

                        System.out.print("수정할 새로운 메세지를 입력해 주세요.\n >> ");
                        String newMessage = scanner.nextLine();
                        messageService.updateMsg(currentUser.getName(),currentMsg, newMessage);
                        System.out.println("기존 내용을 [" + currentMsg.getTextMsg() + "] 로 수정하였습니다.");
                        break;
                    }
                    System.out.println(" ▶ 잘못된 입력입니다. 상위메뉴로 돌아갑니다.");
                    break;
                case 5:               // 1_5 메세지 삭제
                    System.out.println(" ▶ 현재 등록된 메세지는 아래와 같습니다.");
                    messageService.printAllMessages(channelId);
                    System.out.println(" ▶ 삭제할 메세지의 번호를 입력해 주세요");
                    inputMsgNum = scanInt(); // 숫자 입력 메서드 호출
                    currentMsg = messageService.findMessageByNum(channelId,inputMsgNum);
                    System.out.println(" ▶ [" + inputMsgNum + "]번 메세지는 아래와 같습니다.");
                    System.out.println(currentMsg.getTextMsg());

                    System.out.print(" ▶ 정말로 삭제할까요? 삭제를 원하시면 [삭제]라고 입력해 주세요.\n >> ");
                    String deleteConfirm = scanner.nextLine();
                    if (deleteConfirm.length() == 0) {
                        System.out.println(" ▶ 잘못된 입력입니다. 이전 메뉴로 돌아갑니다.");
                        break;
                    } else if (deleteConfirm.equals("삭제")) {
                        messageService.deleteMessage(currentUser.getName(),currentMsg);
                        System.out.println("메세지가 삭제되었습니다.");
                    } else {
                        System.out.println(" ▶ 잘못된 입력입니다. 사용자 삭제를 취소합니다.");
                    }
                    break;
                case 6:               // 1_6 현재 채널 변경
                    currentChannel = ChannelControl.joinChannel();
                    break;
                case 7:               // 1_7 현재 사용자 변경
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
                case 8:               // 1_8 상위메뉴로 이동
                    break;
                default:              // 세부메뉴 입력예외처리
                    System.out.println(" ▶ 잘못된 접근입니다. 다시 입력해 주세요");
            }
            if (choice == 8) {
                break;
            }
        }
    } // 1 메세지 관리 메서드
}
