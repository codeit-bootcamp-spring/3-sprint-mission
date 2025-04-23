package com.sprint;

import com.sprint.mission.discodeit.menu.ChannelMenu;
import com.sprint.mission.discodeit.menu.MessageMenu;
import com.sprint.mission.discodeit.menu.UserMenu;
import com.sprint.mission.discodeit.repository.ChannelDefaultRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.util.Scanner;

public class JavaApplication2 {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        JCFUserRepository jcfUserRepo = new JCFUserRepository();
        JCFChannelRepository jcfChannelRepo = new JCFChannelRepository();
        JCFMessageRepository jcfMessageRepo = new JCFMessageRepository();
        ChannelDefaultRepository.getChannel()
                .forEach(jcfChannelRepo::saveChannel);

        UserService userServiceJCF = new BasicUserService(jcfUserRepo);
        ChannelService channelServiceJCF = new BasicChannelService(jcfChannelRepo);
        MessageService messageServiceJCF = new BasicMessageService(jcfMessageRepo);

        UserMenu userMenu = new UserMenu(sc, userServiceJCF);
        ChannelMenu channelMenu = new ChannelMenu(sc, channelServiceJCF);
        MessageMenu messageMenu = new MessageMenu(sc, messageServiceJCF);

        userMenu.loginUser();

        System.out.println("모든 채널 정보를 출력합니다.");
        channelServiceJCF.outputAllChannelInfo();
        System.out.println("들어가실 채널 번호를 선택해 주세요.");
        int channelNumber = Integer.parseInt(sc.nextLine());
        channelServiceJCF.selectChannel(channelNumber);

        while (true) {
            System.out.println("원하는 기능을 입력 해 주세요.");
            System.out.println("1. 내 정보\t2. 채널 정보\t3. 메시지\t4. 로그아웃");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> userMenu.run();
                case 2 -> channelMenu.run();
                case 3 -> messageMenu.run();
                case 4 -> {
                    System.out.println("Discodeit을 종료합니다. 감사합니다.");
                    sc.close();
                    return;
                }
                default -> System.out.println("올바른 번호를 입력해 주세요.");
            }
        }
    }
}