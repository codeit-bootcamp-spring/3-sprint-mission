package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ChatService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ChatMenu {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ChatService chatService;

    public ChatMenu(UserService userService, ChannelService channelService, MessageService messageService, ChatService chatService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.chatService = chatService;
    }

    public void run(Scanner scanner) {
        boolean isBack = false;
        Channel joinCh = null;
        User joinUser = null;

        // 채널 입장 로직
        while (!isBack) {
            try {
                System.out.println("\n[채널 입장이 필요합니다]");
                // 입장할 유저 ID 입력
                System.out.print("사용자 ID 입력: ");
                String userId = scanner.nextLine();
                joinUser = userService.getUserByUserId(userId);

                // 입장할 채널 입력
                System.out.print("입장할 채널 이름 입력: ");
                String chNm = scanner.nextLine();
                joinCh = channelService.getChannelByChannelName(chNm);

                // 사용자가 채널에 이미 입장되어 있는지 확인
                if (joinUser.getJoinChannelList().contains(joinCh)) {
                    System.out.println("이미 입장한 채널이므로, 채팅 진행 메뉴로 이동합니다.");
                } else {
                    chatService.enterChannel(userId, chNm);
                    System.out.println("채널 입장 ) "+joinCh.getChannelName()+" 에 입장하였습니다.");
                }
                break;
            } catch (NoSuchElementException | IllegalArgumentException | IllegalStateException e) { // 예외 발생시
                System.out.println(e.getMessage());
                isBack = true;
            }
        }


        while (!isBack) {
            int choice = 0;
            Message targetMsg;
            String targetContent;

            // 채팅 진행 안내 멘트 출력
            System.out.println("\n[채팅 진행]");
            System.out.println("1. 메시지 전송");
            System.out.println("2. 메시지 전체 조회");
            System.out.println("3. 메시지 단일 조회 (검색)");
            System.out.println("4. 메시지 수정");
            System.out.println("5. 메시지 삭제");
            System.out.println("6. 채널 퇴장");
            System.out.println("0. 이전 메뉴");

            // 입력 & 입력값이 정수가 아닌 경우 예외처리
            while(true) {
                System.out.print("> ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    break;
                } else {
                    System.out.println("숫자를 입력해주세요!");
                    scanner.nextLine();
                }
            }

            // 입력값에 따라 분기처리 진행
            try {
                switch (choice) {
                    case 1:     // 메시지 생성
                        System.out.print("메시지 입력: ");
                        targetContent = scanner.nextLine();
                        chatService.sendMessage(joinCh, joinUser, targetContent);
                        System.out.println("메시지가 전송되었습니다.");
                        break;
                    case 2:     // 메시지 전체 조회
                        System.out.println("전체 메시지 조회: \n" + messageService.getMessages());
                        break;
                    case 3:     // 메시지 단일 조회 (메시지 내용으로 조회)
                        System.out.print("조회할 메시지 입력: ");
                        targetContent = scanner.nextLine();
                        targetMsg = messageService.getMessageByContent(targetContent);
                        System.out.println(targetMsg);
                        break;
                    case 4:     // 메시지 수정 (메시지 내용으로 조회)
                        System.out.print("수정할 메시지 입력: ");
                        targetContent = scanner.nextLine();
                        targetMsg = messageService.getMessageByContent(targetContent);

                        System.out.print("새로운 메시지 입력: ");
                        String newContent = scanner.nextLine();
                        messageService.updateMessage(targetMsg, newContent);
                        System.out.println("메시지 수정 ) "+ targetMsg.getMsgContent() + "로 수정되었습니다.");
                        break;
                    case 5:     // 메시지 삭제
                        System.out.print("삭제할 메시지 입력: ");
                        targetContent = scanner.nextLine();
                        targetMsg = messageService.getMessageByContent(targetContent);
                        chatService.deleteMessageFromChannel(targetMsg.getId());
                        System.out.println("메시지가 삭제되었습니다.");
                        break;
                    case 6:     // 채널 퇴장
                        chatService.leaveChannel(joinUser, joinCh);
                        System.out.println("채널 퇴장 ) "+joinCh.getChannelName()+"에서 퇴장하였습니다.");
                        isBack = true;
                        break;
                    case 0:     // 이전 메뉴
                        isBack = true;
                        break;
                    default:
                        System.out.println("잘못된 선택입니다.");
                }
            } catch (NoSuchElementException | IllegalArgumentException | IllegalStateException e) { // 예외 발생시
                System.out.println(e.getMessage());
            }
        }
    }
}

