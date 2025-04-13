package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;

import java.util.Scanner;
import java.util.UUID;

public class MessageMenu {
    public static void manageMessages(Scanner scanner, JCFMessageService messageService, JCFUserService userService, JCFChannelService channelService) {
        while (true) {
            System.out.println("\n===== MESSAGE MENU =====");
            System.out.println("1. 메시지 생성");
            System.out.println("2. 메시지 단건 조회");
            System.out.println("3. 메시지 다건 조회");
            System.out.println("4. 메시지 수정");
            System.out.println("5. 메시지 삭제");
            System.out.println("0. 초기 화면으로");
            System.out.print("번호를 입력하세요: ");
            String select = scanner.nextLine();

            try {
                switch (select) {
                    case "1":
                        System.out.print("메시지 내용 입력: ");
                        String content = scanner.nextLine();
                        System.out.print("보낸 사용자 ID: ");
                        UUID sender = UUID.fromString(scanner.nextLine());
                        System.out.print("채널 ID: ");
                        UUID ch = UUID.fromString(scanner.nextLine());
                        Message m = messageService.createMessageCheck(content, sender, ch);
                        System.out.println("생성된 메시지 ID: " + m.getId());
                        break;
                    case "2":
                        System.out.print("메시지 ID 입력: ");
                        UUID id = UUID.fromString(scanner.nextLine());
                        Message find = messageService.getMessage(id);
                        System.out.println(find != null ? find : "메시지를 찾을 수 없습니다.");
                        break;
                    case "3":
                        messageService.getAllMessages().forEach(System.out::println);
                        break;
                    case "4":
                        System.out.print("수정할 메시지 ID 입력: ");
                        UUID upId = UUID.fromString(scanner.nextLine());
                        System.out.print("새 내용 입력: ");
                        String newContent = scanner.nextLine();
                        messageService.updateMessage(upId, newContent);
                        break;
                    case "5":
                        System.out.print("삭제할 메시지 ID 입력: ");
                        UUID delId = UUID.fromString(scanner.nextLine());
                        messageService.deleteMessage(delId);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("올바른 번호를 선택하세요.");
                }
            } catch (Exception e) {
                System.out.println("[오류] 메시지 메뉴 처리 중 문제가 발생했습니다: " + e.getMessage());
            }
        }
    }
}