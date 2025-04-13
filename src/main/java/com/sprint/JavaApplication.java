package com.sprint;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.menu.ChannelMenu;
import com.sprint.mission.discodeit.menu.MessageMenu;
import com.sprint.mission.discodeit.menu.UserMenu;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JavaApplication {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        List<Channel> channels = ChannelRepository.getChannel();

        UserService userService = new JCFUserService(users);
        ChannelService channelService = new JCFChannelService(channels);
        MessageService messageService = new JCFMessageService();

        UserMenu userMenu = new UserMenu(sc, userService, users);
        ChannelMenu channelMenu = new ChannelMenu(sc, channelService, channels);
        MessageMenu messageMenu = new MessageMenu(sc, messageService);

        User user = userService.inputUserName();
        users.add(user);
        System.out.println("로그인 하실 프로필 번호를 입력해 주세요.");
        userService.outputAllUsersInfo();
        int loginNumber = Integer.parseInt(sc.nextLine());
        userService.login(loginNumber, users);
        System.out.println(user.getUsername() + "님 반갑습니다.");

        System.out.println("모든 채널 정보를 출력합니다.");
        channelService.outputAllChannelInfo();
        System.out.println("들어가실 채널 번호를 선택해 주세요.");
        int channelNumber = Integer.parseInt(sc.nextLine());

        while (true) {
            if (channels.isEmpty()) {
                System.out.println("현재 채널이 존재하지 않습니다. DisCodeit을 종료합니다.");
                break;
            }

            System.out.println("원하는 기능을 입력 해 주세요.");
            System.out.println("1. 내 정보\t2. 채널 정보\t3. 메시지\t4. 로그아웃");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> {
                    userMenu.run(user);
                }

                case 2 -> {
                    Channel selectedChannel = channelMenu.run(channelNumber);

                    if (selectedChannel != null) {
                        channelNumber = selectedChannel.getChannelNumber();
                    }
                }
                case 3 -> {
                    messageMenu.run();
                }
                case 4 -> {
                    System.out.println("Discodeit을 종료합니다. 감사합니다.");
                    sc.close();
                    return;
                }
                default -> {
                    System.out.println("올바른 번호를 입력해 주세요.");
                }
            }
        }
    }
}
