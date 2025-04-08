package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import javax.swing.*;
import java.util.Scanner;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit
 * fileName       : JavaApp
 * author         : doungukkim
 * date           : 2025. 4. 8.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 8.        doungukkim       최초 생성
 */
public class JavaApp {
    private static final UserService userService;
    private static final MessageService messageService;
    private static ChannelService channelService;

    static {
        JCFChannelService jcfChannelService = new JCFChannelService();

        JCFUserService jcfUserService = new JCFUserService(jcfChannelService);
        JCFMessageService jcfMessageService = new JCFMessageService(jcfChannelService);

        jcfChannelService.setMessageService(jcfMessageService,jcfUserService);

        channelService = jcfChannelService;
        messageService = jcfMessageService;
        userService = jcfUserService;

    }


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        startDiscodeit();
        User user = createUser(sc, userService);


        while (true) {
//            (1). 회원 이름 수정 // 개인정보 관리
//            (2). 회원 방 조회 usage //=> 메세지 조회, 회원 조회, 메세지 작성, 회원 방 선택
            printPrimaryOptions();

            System.out.println("\n명령을 입력하세요");
            String choice= sc.nextLine();
            switch(choice){
                case "1":
                    userService.updateUsername(user.getId(), sc.nextLine());
                    break;
                case "2":
                    goToUserCli(user, sc);
                    break;
                default:
                    System.out.println("잘못 입력하였습니다.");
            }

        }

    }

    private static void goToUserCli(User user, Scanner sc) {
        while (true) {
            System.out.println("(1). 방 생성");
            System.out.println("(2). 방 선택"); // 메세지 조회(방 기준), 방 초대, 전체 방 조회
            System.out.println("(3). 방 이름 수정\n");
//            printChannelOptions()

            String choice = sc.nextLine();
            Channel selectedChannel;

            switch(choice) {
                case "1":
                    channelService.createChannel(user.getId());
                    break;
                case "2":
                    selectedChannel = getChannelBySelectedNumber(user, sc);
                    if (selectedChannel != null) {
                        boolean chatStatus = true;
                        System.out.println("<" + selectedChannel.getTitle() + ">");
                        selectedChannel.getMessages().forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));
                        System.out.println("---------------");

                        while(chatStatus){
                            System.out.println("<" + selectedChannel.getTitle() + ">");
                            selectedChannel.getMessages().forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));
                            System.out.println("---------------");
                            System.out.println("(0) 채팅 종료\n");
                            String newChat = sc.nextLine();
                            if (newChat.equals("0")) {
                                chatStatus = false;
                            } else {
                                messageService.createMessage(user.getId(), selectedChannel.getId(), newChat);
                                System.out.println("<" + selectedChannel.getTitle() + ">");
                                selectedChannel.getMessages().forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));
                                System.out.println("---------------");
                                System.out.println("(0) 채팅 종료\n");
                            }
                        }
                    } else {
                        System.out.println("방 번호를 입력 해주세요.");
                    }
                    break;

                case "3":
                    selectedChannel = getChannelBySelectedNumber(user, sc);
                    if (selectedChannel!= null) {

                        System.out.println("JavaApp.goToUserCli");
                        System.out.println("selectedChannel = " + selectedChannel);

                        System.out.println("방 번호 입력: ");
                        String channelName = sc.nextLine();
                        channelService.updateChannelName(selectedChannel.getId(), channelName);
                        System.out.println("수정 완료 : " + selectedChannel.getTitle());
                        break;
                    } else {
                        System.out.println("방이 없습니다.");
                        break;
                    }

            }

        }
    }

    private static Channel getChannelBySelectedNumber(User user, Scanner sc) {
        int channelNumber = 0;
        Channel selectedChannel;
        System.out.println("\n방 번호 선택:");
        for (int i = 0; i < channelService.findChannelsByUserId(user.getId()).size(); i++) {
            System.out.println("(" + (i + 1) + ") : " + channelService.findChannelsByUserId(user.getId()).get(i).getTitle());
        }
        channelNumber = sc.nextInt();
        sc.nextLine();

        if (channelService.findChannelsByUserId(user.getId())!= null) {
            selectedChannel = channelService.findChannelsByUserId(user.getId()).get(channelNumber - 1);
            return selectedChannel;
        } else {
            return null;
        }
    }

    private static User createUser(Scanner sc, UserService userService) {
        String choice;
        System.out.println("\n=회원가입=");
        System.out.print("이름:");
        choice= sc.nextLine();
        UUID userId = userService.registerUser(choice);
        System.out.println(choice+"님 회원가입 완료");
        return userService.findUserById(userId);

    }

    private static void printPrimaryOptions(){
        System.out.println("\n(1). 회원 이름 수정"); // 개인정보 관리
        System.out.println("(2). 회원 방 조회"); //=> 메세지 조회, 회원 조회, 메세지 작성, 회원 방 선택
    }

    private static void startDiscodeit() {
        System.out.println(" _____     __     ______     ______     ______     _____     __     ______  ");
        System.out.println("/\\  __-.  /\\ \\   /\\  ___\\   /\\  ___\\   /\\  __ \\   /\\  __-.  /\\ \\   /\\__  _\\ ");
        System.out.println("\\ \\ \\/\\ \\ \\ \\ \\  \\ \\___  \\  \\ \\ \\____  \\ \\ \\/\\ \\  \\ \\ \\/\\ \\ \\ \\ \\  \\/_/\\ \\/ ");
        System.out.println(" \\ \\____-  \\ \\_\\  \\/\\_____\\  \\ \\_____\\  \\ \\_____\\  \\ \\____-  \\ \\_\\    \\ \\_\\ ");
        System.out.println("  \\/____/   \\/_/   \\/_____/   \\/_____/   \\/_____/   \\/____/   \\/_/     \\/_/ ");
        System.out.println("                                                                             ");
        System.out.println("                           :: DiscoDit CLI ::                                ");
        System.out.println();
    }

}
