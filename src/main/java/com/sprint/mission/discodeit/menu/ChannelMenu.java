package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.Scanner;
import java.util.UUID;

public class ChannelMenu {
    public static void manageChannels(Scanner scanner, JCFChannelService channelService, JCFUserService userService) {
        while (true) {
            System.out.println("\n===== CHANNEL MENU =====");
            System.out.println("1. 채널 생성");
            System.out.println("2. 채널 단건 조회");
            System.out.println("3. 채널 다건 조회");
            System.out.println("4. 채널 수정");
            System.out.println("5. 채널 삭제");
            System.out.println("6. 채널에 사용자 추가");
            System.out.println("0. 초기 화면으로");
            System.out.print("번호를 입력하세요: ");
            String select = scanner.nextLine();

            try {
                switch (select) {
                    case "1":
                        System.out.print("채널 이름 입력: ");
                        String name = scanner.nextLine();
                        Channel ch = new Channel(name);
                        channelService.createChannel(ch);
                        System.out.println("생성된 채널 ID: " + ch.getId());
                        break;
                    case "2":
                        System.out.print("채널 ID 입력: ");
                        UUID id = UUID.fromString(scanner.nextLine());
                        Channel found = channelService.getChannel(id);
                        System.out.println(found != null ? found : "채널을 찾을 수 없습니다.");
                        break;
                    case "3":
                        channelService.getAllChannels().forEach(System.out::println);
                        break;
                    case "4":
                        System.out.print("수정할 채널 ID 입력: ");
                        UUID upId = UUID.fromString(scanner.nextLine());
                        System.out.print("새 이름 입력: ");
                        String newName = scanner.nextLine();
                        channelService.updateChannel(upId, newName);
                        break;
                    case "5":
                        System.out.print("삭제할 채널 ID 입력: ");
                        UUID delId = UUID.fromString(scanner.nextLine());
                        channelService.deleteChannel(delId);
                        break;
                    case "6":
                        System.out.print("채널 ID 입력: ");
                        UUID chId = UUID.fromString(scanner.nextLine());
                        System.out.print("사용자 ID 입력: ");
                        UUID userId = UUID.fromString(scanner.nextLine());
                        channelService.addUserToChannel(chId, userId);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("올바른 번호를 선택하세요.");
                }
            } catch (Exception e) {
                System.out.println("[오류] 채널 메뉴 처리 중 문제가 발생했습니다: " + e.getMessage());
            }
        }
    }
}