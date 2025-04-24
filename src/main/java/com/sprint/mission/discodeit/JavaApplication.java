package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.menu.ChannelMenu;
import com.sprint.mission.discodeit.menu.MessageMenu;
import com.sprint.mission.discodeit.menu.UserMenu;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.integration.ChannelIntegration;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.integration.MessageIntegration;
import com.sprint.mission.discodeit.service.jcf.integration.UserIntegration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

// 기존 코드
//public class JavaApplication {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
////        JCF*Service 구현
////        UserService userService = new JCFUserService();
////        MessageService messageService = new JCFMessageService();
////        ChannelService channelService = new JCFChannelService();
//
////        File*Service 구현
//        Path userDir = Paths.get(System.getProperty("user.dir"), "data", "users");
//        Path channelDir = Paths.get(System.getProperty("user.dir"), "data", "channels");
//        Path messageDir = Paths.get(System.getProperty("user.dir"), "data", "messages");
//
//        UserService userService = new FileUserService(userDir);
//        ChannelService channelService = new FileChannelService(channelDir);
//        MessageService messageService = new FileMessageService(messageDir);
//
//        UserIntegration userIntegration = new UserIntegration(userService, channelService);
//        MessageIntegration messageIntegration = new MessageIntegration(messageService, userService, channelService);
//        ChannelIntegration channelIntegration = new ChannelIntegration(channelService, userService, messageService);
//
//        while (true) {
//            System.out.println("\n===== MAIN MENU =====");
//            System.out.println("1. 사용자 메뉴");
//            System.out.println("2. 채널 메뉴");
//            System.out.println("3. 메시지 메뉴");
//            System.out.println("0. 종료");
//            System.out.print("번호를 입력하세요: ");
//
//            String mainChoice = scanner.nextLine();
//
//            try {
//                switch (mainChoice) {
//                    case "1":
//                        UserMenu.manageUsers(scanner, userService, userIntegration);
//                        break;
//                    case "2":
//                        ChannelMenu.manageChannels(scanner, channelService, channelIntegration);
//                        break;
//                    case "3":
//                        MessageMenu.manageMessages(scanner, messageService, messageIntegration);
//                        break;
//                    case "0":
//                        System.out.println("종료합니다.");
//                        return;
//                    default:
//                        System.out.println("올바른 번호를 선택하세요.");
//                }
//            } catch (Exception e) {
//                System.out.println("[오류] 메뉴 처리 중 문제가 발생했습니다: " + e.getMessage());
//            }
//        }
//    }
//}

public class JavaApplication {
    public static void main(String[] args) {
        UserRepository userRepository = new FileUserRepository(Paths.get("data/users"));
        ChannelRepository channelRepository = new FileChannelRepository(Paths.get("data/channels"));
        MessageRepository messageRepository = new FileMessageRepository(Paths.get("data/messages"));

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository);

        UserIntegration userIntegration = new UserIntegration(userService, channelService);
        ChannelIntegration channelIntegration = new ChannelIntegration(channelService, userService, messageService);
        MessageIntegration messageIntegration = new MessageIntegration(messageService, userService, channelService);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. 사용자 메뉴");
            System.out.println("2. 채널 메뉴");
            System.out.println("3. 메시지 메뉴");
            System.out.println("0. 종료");
            System.out.print("번호를 입력하세요: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> UserMenu.manageUsers(scanner, userService, userIntegration);
                case "2" -> ChannelMenu.manageChannels(scanner, channelService, channelIntegration);
                case "3" -> MessageMenu.manageMessages(scanner, messageService, messageIntegration);
                case "0" -> {
                    System.out.println("종료합니다.");
                    return;
                }
                default -> System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }
}

/*
JCF*Service, File*Service와 Basic*Service의 차이
Basic은 저장 로직을 분리해서 서비스에 비즈니스 로직만 위치함
구현체를 생성자로 교체 가능해서 확장성이 좋음
저장소를 변경하더라도 로직에 영향을 안줌
 */