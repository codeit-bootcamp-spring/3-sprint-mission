package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
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
//    private static final UserService userService;
//    private static final MessageService messageService;
//    private static ChannelService channelService;
//
//    static {
//        FileChannelService fileChannelService = new JCFChannelService();
//
//        FileUserService fileUserService = new JCFUserService(fileChannelService);
//        FileMessageService fileMessageService = new JCFMessageService(fileChannelService);
//
//        fileChannelService.setService(fileMessageService,fileUserService);
//
//        channelService = jcfChannelService;
//        messageService = jcfMessageService;
//        userService = jcfUserService;
//
//    }
//
//
//    public static void main(String[] args) {
//
//        Scanner sc = new Scanner(System.in);
//        startDiscodeit();
//        createUser("john", userService);
//        createUser("hannah", userService);
//        createUser("paul", userService);
//
//        System.out.println("\n=회원가입=");
//        System.out.print("이름:");
//        String name = sc.nextLine();
//        User user = createUser(name, userService);
//        boolean exit = true;
//
//        while (exit) {
////            (1). 회원 이름 수정 // 개인정보 관리
////            (2). 회원 방 조회 usage //=> 메세지 조회, 회원 조회, 메세지 작성, 회원 방 선택
////            (0). 프로그램 종료
//            printPrimaryOptions();
//
//            System.out.println("\n명령을 입력하세요");
//            String choice= sc.nextLine();
//            switch(choice){
//                case "1":
//                    userService.updateUsername(user.getId(), sc.nextLine());
//                    break;
//                case "2":
//                    selectChannelOption(user, sc);
//                    break;
//                case "0":
//                    System.out.println("프로그램을 종료합니다..");
//                    exit = false;
//                    break;
//                default:
//                    System.out.println("잘못 입력하였습니다.");
//            }
//
//        }
//
//    }
//
//    private static void selectChannelOption(User user, Scanner sc) {
//        boolean channelStatus = true;
//        while (channelStatus) {
//            System.out.println("(1). 방 생성");
//            System.out.println("(2). 방 선택"); // 메세지 조회(방 기준), 방 초대, 전체 방 조회
//            System.out.println("(3). 방 이름 수정");
//            System.out.println("(0). 뒤로가기\n");
////            printChannelOptions()
//
//            String choice = sc.nextLine();
//            Channel selectedChannel;
//
//            switch(choice) {
//                case "1":
//                    channelService.createChannel(user.getId());
//                    break;
//                case "2":
//                    selectSingleChannelOption(user,sc);
//                    break;
//
////                    selectedChannel = getChannelBySelectedNumber(user, sc);
////                    if (selectedChannel == null) {
////                        break;
////                    }
////                    System.out.println("(1). 유저 초대");
////                    System.out.println("(2). 채팅 시작");
////                    System.out.println("(0). 뒤로 가기");
////                    boolean roomStatus = true;
////                    while(roomStatus){
////                        String shit = sc.nextLine();
////                        switch (shit) {
////                            case "1":
////                                // 유저 초대
////                                List<User> allUsers = userService.findAllUsers();
////                                for (int i = 0; i < allUsers.size(); i++) {
////                                    System.out.println("("+i+"). "+allUsers.get(i).getUsername());
////                                }
////                                User choseUser = allUsers.get(sc.nextInt());
////                                sc.nextLine();
////                                selectedChannel = getChannelBySelectedNumber(user, sc);
////                                if (selectedChannel != null) {
////                                    channelService.addUserInChannel(choseUser.getId(), selectedChannel.getId());
////                                }
////                                break;
////                            case "2":
////                                // 채팅 시작
////                                if (selectedChannel != null) {
////                                    boolean chatStatus = true;
////                                    System.out.println("<" + selectedChannel.getTitle() + ">");
////                                    selectedChannel.getMessages().forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));
////                                    System.out.println("---------------");
////
////                                    while(chatStatus){
////                                        String newChat = sc.nextLine().trim();
////                                        if (newChat.equals("0")) {
////                                            chatStatus = false;
////                                        } else {
////                                            messageService.createMessage(user.getId(), selectedChannel.getId(), newChat);
////                                            System.out.println("<" + selectedChannel.getTitle() + ">");
////                                            selectedChannel.getMessages().forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));
////                                            System.out.println("---------------");
////                                            System.out.println("(0) 채팅 종료\n");
////                                        }
////                                    }
////                                } else {
////                                    System.out.println("방 번호를 입력 해주세요.");
////                                }
////                                break;
////                            case "0":
////                                roomStatus = false;
////                                break;
////                            default:
////                                System.out.println("정확히 입력하세요");
////                        }
////                        break;
////                    }
//
//                case "3":
//                    selectedChannel = getChannelBySelectedNumber(user, sc);
//                    if (selectedChannel!= null) {
//
//                        System.out.println("방 번호 입력: ");
//                        String channelName = sc.nextLine();
//                        channelService.updateChannelName(selectedChannel.getId(), channelName);
//                        System.out.println("수정 완료 : " + selectedChannel.getTitle());
//                        break;
//                    } else {
//                        System.out.println("방이 없습니다.");
//                        break;
//                    }
//                case "0":
//                    channelStatus = false;
//                    break;
//                default:
//                    System.out.println("다시 입력하세요");
//            }
//
//        }
//    }
//
//    private static void selectSingleChannelOption(User user,Scanner sc) {
//        Channel selectedChannel = getChannelBySelectedNumber(user, sc);
//        if (selectedChannel != null) {
//
//            System.out.println("(1). 유저 초대");
//            System.out.println("(2). 채팅 시작");
//            System.out.println("(0). 뒤로 가기");
//            boolean roomStatus = true;
//            while(roomStatus){
//                String shit = sc.nextLine();
//                switch (shit) {
//                    case "1":
//                        // 유저 초대
//                        List<User> allUsers = userService.findAllUsers();
//                        for (int i = 0; i < allUsers.size(); i++) {
//                            System.out.println("("+(i+1)+"). "+allUsers.get(i).getUsername());
//                        }
//                        User choseUser = allUsers.get(sc.nextInt()-1);
//                        sc.nextLine();
//                        selectedChannel = getChannelBySelectedNumber(user, sc);
//                        if (selectedChannel != null) {
//                            channelService.addUserInChannel(selectedChannel.getId(), choseUser.getId());
//                        }
//                        break;
//                    case "2":
//                        // 채팅 시작
//                        if (selectedChannel != null) {
//                            boolean chatStatus = true;
//                            System.out.println("<" + selectedChannel.getTitle() + ">");
//                            selectedChannel.getMessages().forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));
//                            System.out.println("---------------");
//
//                            while(chatStatus){
//                                String newChat = sc.nextLine().trim();
//                                if (newChat.equals("0")) {
//                                    chatStatus = false;
//                                } else {
//                                    messageService.createMessage(user.getId(), selectedChannel.getId(), newChat);
//                                    System.out.println("<" + selectedChannel.getTitle() + ">");
//                                    selectedChannel.getMessages().forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));
//                                    System.out.println("---------------");
//                                    System.out.println("(0) 채팅 종료\n");
//                                }
//                            }
//                        } else {
//                            System.out.println("방 번호를 입력 해주세요.");
//                        }
//                        break;
//                    case "0":
//                        roomStatus = false;
//                        break;
//                    default:
//                        System.out.println("정확히 입력하세요");
//                }
//                break;
//            }
//        }
//    }
//
//    private static Channel getChannelBySelectedNumber(User user, Scanner sc) {
//        int channelNumber = 0;
//        Channel selectedChannel;
//        System.out.println("\n방 번호 선택:");
//        for (int i = 0; i < channelService.findChannelsByUserId(user.getId()).size(); i++) {
//            System.out.println("(" + (i + 1) + ") : " + channelService.findChannelsByUserId(user.getId()).get(i).getTitle());
//        }
//        System.out.println("다른 키를 눌러 뒤로가기");
//        try {
//            channelNumber = sc.nextInt();
//        } catch (Exception e) {
//            return null;
//        }
//        sc.nextLine();
//
//        if (channelService.findChannelsByUserId(user.getId())!= null) {
//            selectedChannel = channelService.findChannelsByUserId(user.getId()).get(channelNumber - 1);
//            return selectedChannel;
//        } else {
//            return null;
//        }
//    }
//
//    private static User createUser(String name, UserService userService) {
//
//        String username = name;
//        UUID userId = userService.registerUser(username);
//        System.out.println(username + "님 회원가입 완료");
//        return userService.findUserById(userId);
//
//    }
//
//    private static void printPrimaryOptions(){
//        System.out.println("\n(1). 회원 이름 수정"); // 개인정보 관리
//        System.out.println("(2). 회원 방 조회"); //=> 메세지 조회, 회원 조회, 메세지 작성, 회원 방 선택
//        System.out.println("(0). 프로그램 종료");
//
//    }
//
//    private static void startDiscodeit() {
//        System.out.println(" _____     __     ______     ______     ______     _____     __     ______  ");
//        System.out.println("/\\  __-.  /\\ \\   /\\  ___\\   /\\  ___\\   /\\  __ \\   /\\  __-.  /\\ \\   /\\__  _\\ ");
//        System.out.println("\\ \\ \\/\\ \\ \\ \\ \\  \\ \\___  \\  \\ \\ \\____  \\ \\ \\/\\ \\  \\ \\ \\/\\ \\ \\ \\ \\  \\/_/\\ \\/ ");
//        System.out.println(" \\ \\____-  \\ \\_\\  \\/\\_____\\  \\ \\_____\\  \\ \\_____\\  \\ \\____-  \\ \\_\\    \\ \\_\\ ");
//        System.out.println("  \\/____/   \\/_/   \\/_____/   \\/_____/   \\/_____/   \\/____/   \\/_/     \\/_/ ");
//        System.out.println("                                                                             ");
//        System.out.println("                           :: DiscoDit CLI ::                                ");
//        System.out.println();
//    }

}
