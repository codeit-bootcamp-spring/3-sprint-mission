package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ChatService;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ChannelMenu {

    private final ChannelService channelService;
    private final ChatService chatService;

    public ChannelMenu(ChannelService channelService, ChatService chatService) {
        this.channelService = channelService;
        this.chatService = chatService;
    }

    public void run(Scanner scanner) {
        boolean isBack = false;

        while (!isBack) {
            Channel targetCh;
            String targetChNm;
            int choice = 0;

            // 채널 관리 안내 멘트 출력
            System.out.println("\n[채널 관리]");
            System.out.println("1. 채널 생성");
            System.out.println("2. 채널 전체 조회");
            System.out.println("3. 채널 단일 조회 (검색)");
            System.out.println("4. 채널 이름 수정");
            System.out.println("5. 채널 삭제");
            System.out.println("0. 이전 메뉴");

            // 입력 & 입력값이 정수가 아닌 경우 예외처리
            while (true) {
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
                    case 1:     // 채널 생성
                        System.out.print("채널 이름 입력: ");
                        targetChNm = scanner.nextLine();
                        targetCh = channelService.createChannel(targetChNm);
                        System.out.println("채널 생성 ) " + targetCh.getChannelName() + " 생성되었습니다.");
                        break;
                    case 2:     // 채널 전체 조회
                        System.out.println("전체 채널 목록: \n" + channelService.getChannels());
                        break;
                    case 3:     // 채널 단일 조회 (채널명으로 조회)
                        System.out.print("조회할 채널 이름 입력: ");
                        targetChNm = scanner.nextLine();
                        targetCh = channelService.getChannelByChannelName(targetChNm);
                        System.out.println(targetCh);
                        break;
                    case 4:     // 채널 이름 수정
                        System.out.print("수정할 채널명 입력: ");
                        targetChNm = scanner.nextLine();
                        targetCh = channelService.getChannelByChannelName(targetChNm);

                        System.out.print("새로운 채널명 입력: ");
                        String newChNm = scanner.nextLine();
                        channelService.updateChannel(targetCh, newChNm);
                        System.out.println("채널명 수정 ) "+ targetCh.getChannelName() +"로 수정되었습니다.");
                        break;
                    case 5:     // 채널 삭제
                        System.out.print("삭제할 채널 이름 입력: ");
                        targetChNm = scanner.nextLine();
                        targetCh = channelService.getChannelByChannelName(targetChNm);
                        chatService.deleteChannelFromUsers(targetCh.getId());
                        System.out.println("채널 삭제 ) "+ targetCh.getChannelName() +" 삭제되었습니다.");
                        break;
                    case 0:     // 이전 메뉴
                        isBack = true;
                        break;
                    default:
                        System.out.println("잘못된 선택입니다.");
                        break;
                }
            } catch (NoSuchElementException | IllegalArgumentException | IllegalStateException e) { // 예외 발생시
                System.out.println(e.getMessage());
            }
        }
    }
}
