package com.sprint;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JavaApplication {

    static Scanner sc = new Scanner(System.in);
    static List<User> users = new ArrayList<>();

    static JCFUserService JCFUserService = new JCFUserService(users);
    static List<Channel> channels = ChannelRepository.getChannel();
    static JCFChannelService JCFChannelService = new JCFChannelService(channels);
    static JCFMessageService messageService = new JCFMessageService();
    static List<Message> messages = new ArrayList<>();


    public static void login(int loginNumber, List<User> users) {
        users.stream()
                .filter(user -> user.getNumber() == loginNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 번호의 유저가 존재하지 않습니다: "));
    }

    private static void handleUserMenu(User user, JCFUserService JCFUserService) {

        while (true) {
            System.out.println(
                    "1.새로운 프로필 추가\t2.모든 프로필 정보 출력\t3.프로필 정보 출력\t4.프로필 이름 수정\t5.프로필 정보 삭제\t6.프로필 변경\t7.이전 메뉴");

            int n = Integer.parseInt(sc.nextLine());
            if (n == 1) {
                System.out.println("새롭게 추가 할 프로필 이름을 입력해 주세요.");
                String newUser = sc.nextLine();
                JCFUserService.createNewUserNames(user.getUsername(), newUser);
            }
            if (n == 2) {
                JCFUserService.outputAllUsersInfo();
            }
            if (n == 3) {
                JCFUserService.outputOneUserInfo(user.getUsername());
            }
            if (n == 4) {
                System.out.println("현재 사용자 이름은 " + user.getUsername() + "입니다. 새로운 이름을 입력해 주세요.");
                String updateUserName = sc.nextLine();
                JCFUserService.updateUserName(user.getUsername(), updateUserName);
            }
            if (n == 5) {
                JCFUserService.outputAllUsersInfo();
                System.out.println("삭제 할 사용자의 번호를 입력하세요.");
                int num = Integer.parseInt(sc.nextLine());
                System.out.println(user.getUsername() + " 정보를 삭제합니다.");
                JCFUserService.deleteUserName(num);
            }
            if (n == 6) {
                System.out.println("변경하실 프로필 번호를 입력하세요.");
                JCFUserService.outputAllUsersInfo();
                int loginNumber = Integer.parseInt(sc.nextLine());
                login(loginNumber, users);
                user = JCFUserService.changeUser(loginNumber);
            }

            if (n == 7) {
                break;
            }
        }

    }

    private static void handleChannelMenu(int channelNumber, List<Channel> channels,
                                          JCFChannelService JCFChannelService) {
        Channel selectChannel = channels.stream().filter(channel1 -> channel1.getChannelNumber() == channelNumber)
                .findFirst().orElse(null);

        while (true) {
            System.out.println("1.채널 생성\t2.현재 채널 정보 출력\t3.모든 채널 정보 출력\t4.채널 이름 수정\t5.채널 삭제\t6.채널 변경\t7.이전 메뉴");
            int n = Integer.parseInt(sc.nextLine());
            if (n == 1) {
                System.out.println("새로 추가하실 채널 이름을 입력해 주세요.");
                String newChannelName = sc.nextLine();
                JCFChannelService.createNewChannel(newChannelName);
            }
            if (n == 2) {
                JCFChannelService.outputOneChannelInfo(selectChannel);
            }
            if (n == 3) {
                JCFChannelService.outputAllChannelInfo();
            }
            if (n == 4) {
                assert selectChannel != null;
                System.out.println(
                        "현재 접속중인 채널은" + selectChannel.getChannelName() + "입니다. 새로운 채널 이름을 작성 해 주세요.");
                String updateChannelName = sc.nextLine();
                JCFChannelService.updateChannelName(selectChannel.getChannelName(), updateChannelName);
            }
            if (n == 5) {
                assert selectChannel != null;
                System.out.println(selectChannel.getChannelName() + " 채널을 삭제합니다.");
                JCFChannelService.deleteChannelName(selectChannel.getChannelName());
                n = 7;
            }
            if (n == 6) {
                System.out.println("변경하실 채널 번호를 입력해주세요.");
                JCFChannelService.outputAllChannelInfo();
                int newChannelNumber = Integer.parseInt(sc.nextLine());
                selectChannel = JCFChannelService.changeChannel(newChannelNumber);
            }

            if (n == 7) {
                break;
            }
        }
    }


    private static void handleMessageMenu(JCFMessageService messageService, User user) {
        while (true) {
            System.out.println("1.메시지 작성\t2.작성한 메시지 출력\t3.메시지 수정\t4.메시지 삭제\t5.이전 메뉴로 돌아가기");
            int n = Integer.parseInt(sc.nextLine());
            if (n == 1) {
                System.out.println("작성하실 메시지 입력해 주세요");
                String messageText = sc.nextLine();
                messages = messageService.inputMessage(messageText);
                System.out.println("메시지 작성 완료!");
            }
            if (n == 2) {
                messageService.outputUserMessage();
            }
            if (n == 3) {
                System.out.println("수정 할 메시지의 번호를 입력해 주세요.");
                messageService.outputUserMessage();
                int messageNumber = Integer.parseInt(sc.nextLine());
                System.out.println("메시지를 입력해 주세요.");
                String newMessage = sc.nextLine();
                messageService.updateUserMessage(messageNumber, newMessage);
            }
            if (n == 4) {
                System.out.println("삭제한 메시지의 번호를 입력해 주세요.");
                messageService.outputUserMessage();
                int deleteMessageNumber = Integer.parseInt(sc.nextLine());
                messageService.deleteUserMessage(deleteMessageNumber);
                System.out.println("삭제가 완료됐습니다.");
            }

            if (n == 5) {
                break;
            }
        }
    }

    private static void inputDefaultUser(User user) {

        int loginNumber = Integer.parseInt(sc.nextLine());
        login(loginNumber, users);
        System.out.println(user.getUsername() + "님 반갑습니다 ");
    }

    public static void main(String[] args) {

        User user = JCFUserService.inputUserName();
        users.add(user);

        System.out.println("로그인 하실 프로필 번호를 입력해 주세요.");
        JCFUserService.outputAllUsersInfo();
        inputDefaultUser(user);

        System.out.println("모든 채널 정보를 출력합니다.");
        JCFChannelService.outputAllChannelInfo();
        System.out.println("들어가실 채널번호를 선택해 주세요.");
        int channelNumber = Integer.parseInt(sc.nextLine());

        while (true) {

            if (channels.isEmpty()) {
                System.out.println("현재 채널이 존재하지 않습니다. DisCodeit을 종료합니다.");
                return;
            }

            System.out.println("원하는 기능을 입력 해 주세요.");
            System.out.println("1.내정보\t2.채널정보\t3.메시지\t4.로그아웃");
            int num = Integer.parseInt(sc.nextLine());

            if (num == 1) {
                handleUserMenu(user, JCFUserService);
            }
            if (num == 2) {
                handleChannelMenu(channelNumber, channels, JCFChannelService);
            }
            if (num == 3) {
                handleMessageMenu(messageService, user);

            }
            if (num == 4) {
                System.out.println("Discodeit을 종료합니다. 감사합니다.");
                break;
            }

        }
    }

}
