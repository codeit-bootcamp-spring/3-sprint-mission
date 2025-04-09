package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.Scanner;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        JCFUserService userService = new JCFUserService();
        JCFMessageService messageService = new JCFMessageService(userService, null);
        JCFChannelService channelService = new JCFChannelService(messageService, userService);

        messageService.setChannelService(channelService);
        userService.addChannelService(channelService);

        while (true) {
            System.out.println("===== MAIN MENU =====");
            System.out.println("1. 사용자 메뉴");
            System.out.println("2. 채널 메뉴");
            System.out.println("3. 메시지 메뉴");
            System.out.println("0. 종료");
            System.out.println("번호를 입력하세요");
            String mainMenu = scanner.nextLine();

            switch (mainMenu) {
                case "1":
                    userMenu(scanner, userService);
                    break;
                case "2":
                    channelMenu(scanner, channelService, userService);
                    break;
                case "3":
                    messageMenu(scanner, messageService, channelService, userService);
                    break;
                case "0":
                    System.out.println("종료");
                    return; 
                default:
                    System.out.println("표시되지 않은 번호를 눌렀습니다");
            }
        }
    }

    private static void userMenu(Scanner scanner, JCFUserService userService) {
        while (true) {
            System.out.println("===== USER MENU =====");
            System.out.println("1. 사용자 등록");
            System.out.println("2. 사용자 단건 조회");
            System.out.println("3. 사용자 다건 조회");
            System.out.println("4. 사용자 수정");
            System.out.println("5. 사용자 삭제");
            System.out.println("0. 초기 화면으로");
            System.out.println("번호를 입력하세요");
            String select = scanner.nextLine();

            switch (select) {
                case "1":
                    System.out.println("사용자 이름 입력");
                    String userName = scanner.nextLine();
                    User user = new User(userName);
                    userService.createUser(user);
                    System.out.println("생성된 사용자 식별키 : " + user.getId());
                    break;
                case "2":
                    System.out.println("조회할 사용자의 식별키 입력");
                    UUID id = UUID.fromString(scanner.nextLine());
                    User userId = userService.getUser(id);
                    if (userId != null) {
                        System.out.println("사용자 이름 : " + userId.getUserName());
                    } else {
                        System.out.println("해당 식별키를 가진 사용자가 없습니다");
                    }
                    break;
                case "3":
                    System.out.println("전체 사용자 목록");
                    userService.getAllUsers()
                            .forEach(u -> System.out.println(u.getId()
                                    + " - " + u.getUserName()));
                case "4":
                    System.out.println("수정할 사용자 식별키 입력");
                    UUID updateId = UUID.fromString(scanner.nextLine());
                    System.out.println("새로운 사용자 이름 입력");
                    String newUserName = scanner.nextLine();
                    userService.updateUser(updateId, newUserName);
                    System.out.println("수정 완료");
                    break;
                case "5":
                    System.out.println("삭제할 사용자 식별키 입력");
                    UUID deleteId = UUID.fromString(scanner.nextLine());
                    userService.deleteUser(deleteId);
                    System.out.println("삭제 완료");
                    break;
                case "0":
                    return;
                default:
                    System.out.println("표시되지 않은 번호를 눌렀습니다");
            }
        }
    }

    private static void channelMenu(Scanner scanner, JCFChannelService channelService, JCFUserService userService) {
        while (true) {
            System.out.println("===== CHANNEL MENU =====");
            System.out.println("1. 채널 생성");
            System.out.println("2. 채널 단건 조회");
            System.out.println("3. 채널 다건 조회");
            System.out.println("4. 채널 수정");
            System.out.println("5. 채널 삭제");
            System.out.println("6. 채널에 사용자 추가");
            System.out.println("0. 초기 화면으로");
            System.out.println("번호를 입력하세요");
            String select = scanner.nextLine();

            switch (select) {
                case "1":
                    System.out.println("채널 이름 입력");
                    String channelName = scanner.nextLine();
                    Channel channel = new Channel(channelName);
                    channelService.createChannel(channel);
                    System.out.println("생성된 채널 식별키 : " + channel.getId());
                    break;
                case "2":
                    System.out.println("조회할 채널 식별키 입력");
                    UUID channelId = UUID.fromString(scanner.nextLine());
                    Channel find = channelService.getChannel(channelId);
                    if (find != null) {
                        System.out.println("채널 이름 : " + find.getChannelName());
                        System.out.println("참가자 : " + find.getUserIds());
                    } else {
                        System.out.println("해당 식별키를 가진 채널이 없습니다");
                    }
                    break;
                case "3":
                    channelService.getAllChannels()
                            .forEach(ch -> System.out.println(ch.getId()
                                    + " - " + ch.getChannelName()));
                    break;
                case "4":
                    System.out.println("수정할 채널 식별키 입력");
                    UUID updateCh = UUID.fromString(scanner.nextLine());
                    System.out.println("새로운 채널 이름 입력");
                    String newChannelName = scanner.nextLine();
                    channelService.updateChannel(updateCh, newChannelName);
                    break;
                case "5":
                    System.out.println("삭제할 채널 식별키 입력");
                    UUID deleteCh = UUID.fromString(scanner.nextLine());
                    channelService.deleteChannel(deleteCh);
                    break;
                case "6":
                    System.out.println("채널 식별키 입력");
                    UUID joinCh = UUID.fromString(scanner.nextLine());
                    System.out.println("등록할 사용자 식별키 입력");
                    UUID userJoin = UUID.fromString(scanner.nextLine());
                    channelService.addUserToChannel(joinCh, userJoin);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("표시되지 않은 번호를 눌렀습니다");
            }
        }
    }

    private static void messageMenu(Scanner scanner, JCFMessageService messageService, JCFChannelService channelService, JCFUserService userService) {
        while (true) {
            System.out.println("===== MESSAGE MENU =====");
            System.out.println("1. 메시지 생성");
            System.out.println("2. 메시지 단건 조회");
            System.out.println("3. 메시지 다건 조회");
            System.out.println("4. 메시지 수정");
            System.out.println("5. 메시지 삭제");
            System.out.println("0. 초기 화면으로");
            System.out.println("번호를 입력하세요");
            String select = scanner.nextLine();

            switch (select) {
                case "1":
                    System.out.print("보낼 메시지 내용");
                    String content = scanner.nextLine();
                    System.out.print("보낸 사용자 식별키");
                    UUID senderId = UUID.fromString(scanner.nextLine());
                    System.out.print("보낼 채널 식별키");
                    UUID channelId = UUID.fromString(scanner.nextLine());
                    Message message = messageService.createMessageCheck(content, senderId, channelId);
                    System.out.println("생성된 메시지 식별키 " + message.getId());
                    break;
                case "2":
                    System.out.print("조회할 메시지 식별키 입력");
                    UUID messageId = UUID.fromString(scanner.nextLine());
                    Message msg = messageService.getMessage(messageId);
                    if (msg != null) {
                        System.out.println(msg);
                    } else {
                        System.out.println("메시지를 찾을 수 없습니다.");
                    }
                    break;
                case "3":
                    messageService.getAllMessages().forEach(System.out::println);
                    break;
                case "4":
                    System.out.print("수정할 메시지 식별키 입력");
                    UUID msgId = UUID.fromString(scanner.nextLine());
                    System.out.print("새 메시지 내용 입력");
                    String newMsg = scanner.nextLine();
                    messageService.updateMessage(msgId, newMsg);
                    break;
                case "5":
                    System.out.print("삭제할 메시지 식별키 입력");
                    UUID delId = UUID.fromString(scanner.nextLine());
                    messageService.deleteMessage(delId);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("표시되지 않은 번호를 눌렀습니다");
            }
        }
    }
}
