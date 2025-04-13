package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Scanner;

public class ChannelMenu {
    private final Scanner scanner;
    private final ChannelService channelService;
    private final List<Channel> channels;

    public ChannelMenu(Scanner scanner, ChannelService channelService, List<Channel> channels) {
        this.scanner = scanner;
        this.channelService = channelService;
        this.channels = channels;
    }

    public Channel run(int channelNumber) {
        Channel selectChannel = channels.stream()
                .filter(ch -> ch.getChannelNumber() == channelNumber)
                .findFirst().orElse(null);

        while (true) {
            System.out.println("1. 채널 생성\t2. 현재 채널 정보 출력\t3. 모든 채널 정보 출력 \t4. 채널 이름 수정\t5. 채널 삭제\t6. 채널 변경\t7. 이전 메뉴");
            int n = Integer.parseInt(scanner.nextLine());

            switch (n) {
                case 1 -> {
                    System.out.println("새로 추가하실 채널 이름을 입력해 주세요.");
                    String newChannelName = scanner.nextLine();
                    channelService.createNewChannel(newChannelName);
                }
                case 2 -> channelService.outputOneChannelInfo(selectChannel);
                case 3 -> channelService.outputAllChannelInfo();
                case 4 -> {
                    if (selectChannel != null) {
                        System.out.println(
                                "현재 접속중인 채널은 " + selectChannel.getChannelName() + "입니다. 새로운 채널 이름을 입력해 주세요.");
                        String updateChannelName = scanner.nextLine();
                        channelService.updateChannelName(selectChannel.getChannelName(), updateChannelName);
                    }
                }
                case 5 -> {
                    if (selectChannel != null) {
                        System.out.println(selectChannel.getChannelName() + " 채널을 삭제합니다.");
                        channelService.deleteChannelName(selectChannel.getChannelName());
                        System.out.println("정상적으로 채널이 삭제됐습니다. 변경하실 채널 번호를 입력해주세요.");
                        channelService.outputAllChannelInfo();
                        int newChannelNumber = Integer.parseInt(scanner.nextLine());
                        selectChannel = channelService.changeChannel(newChannelNumber);
                    }
                }
                case 6 -> {
                    System.out.println("변경하실 채널 번호를 입력해주세요.");
                    channelService.outputAllChannelInfo();
                    int newChannelNumber = Integer.parseInt(scanner.nextLine());
                    selectChannel = channelService.changeChannel(newChannelNumber);
                }
                case 7 -> {
                    return selectChannel;
                }
                default -> System.out.println("올바른 번호를 입력해 주세요.");
            }
        }
    }
}
