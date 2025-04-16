package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.menu.ChannelMenu;
import com.sprint.mission.discodeit.menu.MessageMenu;
import com.sprint.mission.discodeit.menu.UserMenu;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.integration.ChannelIntegration;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.integration.MessageIntegration;
import com.sprint.mission.discodeit.service.jcf.integration.UserIntegration;

import java.util.Scanner;

public class JavaApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new JCFUserService();
        MessageService messageService = new JCFMessageService();
        ChannelService channelService = new JCFChannelService();

        UserIntegration userIntegration = new UserIntegration(userService, channelService);
        MessageIntegration messageIntegration = new MessageIntegration(messageService, userService, channelService);
        ChannelIntegration channelIntegration = new ChannelIntegration(channelService, userService, messageService);

        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. 사용자 메뉴");
            System.out.println("2. 채널 메뉴");
            System.out.println("3. 메시지 메뉴");
            System.out.println("0. 종료");
            System.out.print("번호를 입력하세요: ");

            String mainChoice = scanner.nextLine();

            try {
                switch (mainChoice) {
                    case "1":
                        UserMenu.manageUsers(scanner, userService, userIntegration);
                        break;
                    case "2":
                        ChannelMenu.manageChannels(scanner, channelService, channelIntegration);
                        break;
                    case "3":
                        MessageMenu.manageMessages(scanner, messageService, messageIntegration);
                        break;
                    case "0":
                        System.out.println("종료합니다.");
                        return;
                    default:
                        System.out.println("올바른 번호를 선택하세요.");
                }
            } catch (Exception e) {
                System.out.println("[오류] 메뉴 처리 중 문제가 발생했습니다: " + e.getMessage());
            }
        }
    }
}