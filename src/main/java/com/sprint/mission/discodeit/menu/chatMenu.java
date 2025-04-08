package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class chatMenu {
    private final JCFUserService userService;
    private final JCFChannelService channelService;
    private final JCFMessageService messageService;

    public chatMenu(JCFUserService userService, JCFChannelService channelService, JCFMessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    public void run(Scanner scanner) {
        boolean back = false;
        Channel joinCh = null;
        User joinUser = null;

        // 채널 입장 로직
        while (true) {
            try {
                System.out.println("\n[채널 입장이 필요합니다]");
                // 입장할 유저 ID 입력
                System.out.print("사용자 ID 입력: ");
                String userId = scanner.nextLine();
                joinUser = userService.searchUserByUserId(userId);

                // 입장할 채널 입력
                System.out.print("입장할 채널 이름 입력: ");
                String chNm = scanner.nextLine();
                joinCh = channelService.searchChannelByChannelName(chNm);

                // 채널 입장
                channelService.enterChannel(joinUser, joinCh);
                break;
            } catch (NoSuchElementException | IllegalArgumentException | IllegalStateException e) { // 예외 발생시
                System.out.println(e.getMessage());
            }
        }


        while (!back) {
            int choice = 0;
            Message targetMsg;
            String targetContent;

            // 채팅 진행 안내 멘트 출력
            System.out.println("\n[채팅 진행]");
            System.out.println("1. 메시지 생성");
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
                        messageService.createMessage(joinCh, joinUser, targetContent);
                        break;
                    case 2:     // 메시지 전체 조회
                        System.out.println("전체 메시지 조회: \n" + messageService.getMessages());
                        break;
                    case 3:     // 메시지 단일 조회 (메시지 내용으로 조회)
                        System.out.print("조회할 메시지 입력: ");
                        targetContent = scanner.nextLine();
                        targetMsg = messageService.searchContentByMessage(targetContent);
                        System.out.println(targetMsg);
                        break;
                    case 4:     // 메시지 수정 (메시지 내용으로 조회)
                        System.out.print("수정할 메시지 입력: ");
                        targetContent = scanner.nextLine();
                        targetMsg = messageService.searchContentByMessage(targetContent);

                        System.out.print("새로운 메시지 입력: ");
                        String newContent = scanner.nextLine();
                        messageService.updateMessage(targetMsg, newContent);
                        break;
                    case 5:     // 메시지 삭제
                        System.out.print("삭제할 메시지 입력: ");
                        targetContent = scanner.nextLine();
                        targetMsg = messageService.searchContentByMessage(targetContent);
                        messageService.deleteMessage(targetMsg.getId());
                        break;
                    case 6:     // 채널 퇴장
                        channelService.leaveChannel(joinUser, joinCh);
                        back = true;
                        break;
                    case 0:     // 이전 메뉴
                        back = true;
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

