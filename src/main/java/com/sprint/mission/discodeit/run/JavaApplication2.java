package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.menu.ChannelMenu;
import com.sprint.mission.discodeit.menu.ChatMenu;
import com.sprint.mission.discodeit.menu.UserMenu;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ChatService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicChatService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.Scanner;


public class JavaApplication2 {

    public static void main(String[] args) {
        /* 스프린트 미션 2 구현 메서드 */

        // CASE 2. File*Service 구현체를 이용한 테스트
//        UserService userService = new FileUserService();
//        MessageService messageService = new FileMessageService();
//        ChannelService channelService = new FileChannelService();
//        ChatService chatService = new FileChatService(messageService,channelService,userService);

        // CASE 3. Basic*Service 구현체를 활용한 테스트
        // CASE 3-1. JCF*Repository 구현체를 활용한 테스트
//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();

        // CASE 3-2. File*Repository 구현체를 활용한 테스트
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository);
        ChatService chatService = new BasicChatService(channelService, messageService, userService);

        boolean isRunning = true;

        try (Scanner scanner = new Scanner(System.in)) {
            while (isRunning) {
                int choice = 0;
                UserMenu userMenu = new UserMenu(userService);
                ChannelMenu channelMenu = new ChannelMenu(channelService, chatService);
                ChatMenu chatMenu = new ChatMenu(userService,channelService, messageService, chatService);

                System.out.println("========= 디스코드잇 =========");
                System.out.println("1. 유저 관리");
                System.out.println("2. 채널 관리");
                System.out.println("3. 채팅 관리");
                System.out.println("0. 종료 ");

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

                switch (choice) {
                    case 1:
                        userMenu.run(scanner);
                        break;
                    case 2:
                        channelMenu.run(scanner);
                        break;
                    case 3:
                        chatMenu.run(scanner);
                        break;
                    case 0:
                        isRunning = false;
                        System.out.println("프로그램을 종료합니다.");
                        break;
                    default:
                        System.out.println("잘못된 선택입니다.");
                }
            }
        }
    }
}
