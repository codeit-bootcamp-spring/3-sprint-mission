package com.sprint.mission.discodeit.v1;

import com.sprint.mission.discodeit.v1.entity.Channel1;
import com.sprint.mission.discodeit.v1.entity.Message1;
import com.sprint.mission.discodeit.v1.entity.User1;
import com.sprint.mission.discodeit.v1.service.ChannelService1;
import com.sprint.mission.discodeit.v1.service.MessageService1;
import com.sprint.mission.discodeit.v1.service.UserService1;
import com.sprint.mission.discodeit.v1.service.file.FileChannelService1;
import com.sprint.mission.discodeit.v1.service.file.FileMessageService1;
import com.sprint.mission.discodeit.v1.service.file.FileUserService1;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.util
 * fileName       : JavaApplicationCli
 * author         : doungukkim
 * date           : 2025. 4. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 16.        doungukkim       최초 생성
 */
public class JavaApplicationCli1 {
    private static final UserService1 userService;
    public static final MessageService1 messageService;
    public static ChannelService1 channelService;
    private static Scanner sc = new Scanner(System.in);

    private static UUID userId;
    private static UUID channelId;
    private static UUID messageId;

    static {
        // 1. 구현체 먼저 생성
        FileChannelService1 fileChannelService = new FileChannelService1();
        FileUserService1 fileUserService = new FileUserService1(fileChannelService);
        FileMessageService1 fileMessageService = new FileMessageService1(fileChannelService);
        // 2. 순환 의존 setter
        fileChannelService.setService(fileMessageService, fileUserService);

        // 3. 인터페이스로 노출
        channelService = fileChannelService;
        messageService = fileMessageService;
        userService = fileUserService;
    }

    public static void main(String[] args) {
        System.out.println("로그인 이름 입력: ");
        String name = sc.nextLine();
        userId = userService.registerUser(name);

        boolean loop = true;

        System.out.println("| 1) 채널 | 2) 메세지 | 3) 유저 | 0) 종료 |");
        while (loop) {
            System.out.println("입력: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    channelTest();
                    System.out.println("| 1) 채널 | 2) 메세지 | 3) 유저 | 0) 종료 |");
                    break;
                case "2":
                    messageTest();
                    System.out.println("| 1) 채널 | 2) 메세지 | 3) 유저 | 0) 종료 |");
                    break;
                case "3":
                    userTest();
                    System.out.println("| 1) 채널 | 2) 메세지 | 3) 유저 | 0) 종료 |");
                    break;
                case "0":
                    System.out.println("프로그램 종료..");
                    loop = false;
                    break;
                default:
                    System.out.println("잘못 입력하였습니다.");
            }
        }
    }

    private static void userTest() {
        boolean loop = true;
        System.out.println("| 1) 단건 조회 | 2) 다건 조회 | 3) 이름 수정 | 4) 유저 삭제 | 0) 뒤로가기");
        while (loop) {
            System.out.println("입력: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    singleUserSearchTest();
                    System.out.println("| 1) 단건 조회 | 2) 다건 조회 | 3) 이름 수정 | 4) 유저 삭제 | 0) 뒤로가기");
                    break;
                case "2":
                    multipleUserSearchTest();
                    System.out.println("| 1) 단건 조회 | 2) 다건 조회 | 3) 이름 수정 | 4) 유저 삭제 | 0) 뒤로가기");
                    break;
                case "3":
                    updateUsername();
                    System.out.println("| 1) 단건 조회 | 2) 다건 조회 | 3) 이름 수정 | 4) 유저 삭제 | 0) 뒤로가기");
                    break;
                case "4":
                    deleteUserTest();
                    System.out.println("| 1) 단건 조회 | 2) 다건 조회 | 3) 이름 수정 | 4) 유저 삭제 | 0) 뒤로가기");
                    break;
                case "0":
                    System.out.println("유저 테스트 종료...");
                    loop = false;
                    break;
                default:
                    System.out.println("잘못 입력하였습니다.");
            }
        }
    }
    private static void singleUserSearchTest() {
        System.out.println("단건 조회");
        userService.findAllUsers().forEach(user -> System.out.println(user.getUsername()+" : "+user.getId()));
        System.out.println("입력 :");
        String message = sc.nextLine();
        if(!message.equals("0")){
            try{
                User1 user = userService.findUserById(UUID.fromString(message));
                System.out.println(user.getUsername()+" : "+user.getId());
                user.getChannelIds().forEach(channelId -> System.out.println("참여 채널ID : "+channelId));
            } catch (Exception e) {
                System.out.println("잘못된 입력");
            }
        }
    }

    private static void multipleUserSearchTest() {
        System.out.println("다건 조회");
        userService.findAllUsers().forEach(user -> System.out.println(user.getUsername()+" : "+user.getId()));
    }

    private static void updateUsername() {
        System.out.println("이름 수정 (0)뒤로가기");
        User1 userById = userService.findUserById(userId);
        String username = sc.nextLine();
        if (!username.equals("0")) {
            try{
                System.out.println("before : " + userById.getUsername() + " : " + userById.getId());
                userService.updateUsername(userId, username);
                System.out.println("after : " + userById.getUsername() + " : " + userById.getId());
            } catch (Exception e) {
                System.out.println("wrong input");
            }
        }
    }

    private static void deleteUserTest() {
        multipleUserSearchTest();
        System.out.println("유저 삭제");
        System.out.println("입력:");
        String id = sc.nextLine();
        if (!id.equals("0")) {
            try{
                userService.deleteUser(UUID.fromString(id));
                multipleUserSearchTest();
            } catch (Exception e) {
                System.out.println("wrong input..");
            }
        }
    }

    private static void messageTest() {
        boolean loop = true;
        System.out.println("| 1) 메세지 작성 | 2) 단건 조회 | 3) 다건 조회 | 4) 메세지 수정 | 5) 메세지 삭제 | 0) 뒤로가기");
        while (loop) {
            System.out.println("입력: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    // 채널 선택 by channelId
                    singleChannelSearchTest();
                    // 메세지 작성
                    createMessageTest();
                    System.out.println("| 1) 메세지 작성 | 2) 단건 조회 | 3) 다건 조회 | 4) 메세지 수정 | 5) 메세지 삭제 | 0) 뒤로가기");
                    break;
                case "2":
                    // 채널 선택
                    singleChannelSearchTest();
                    // 메세지 선택
                    singleMessageSearchTest();
                    System.out.println("| 1) 메세지 작성 | 2) 단건 조회 | 3) 다건 조회 | 4) 메세지 수정 | 5) 메세지 삭제 | 0) 뒤로가기");
                    break;
                case "3":
                    multipleMessageSearchTest();
                    System.out.println("| 1) 메세지 작성 | 2) 단건 조회 | 3) 다건 조회 | 4) 메세지 수정 | 5) 메세지 삭제 | 0) 뒤로가기");
                    break;
                case "4":
                    // 채널 선택
                    singleChannelSearchTest();
                    // 메세지 선택
                    singleMessageSearchTest();
                    // 메세지 수정
                    updateMessageTitleTest();
                    System.out.println("| 1) 메세지 작성 | 2) 단건 조회 | 3) 다건 조회 | 4) 메세지 수정 | 5) 메세지 삭제 | 0) 뒤로가기");;
                    break;
                case "5":
                    // 채널 선택
                    singleChannelSearchTest();
                    // 메세지 선택
                    singleMessageSearchTest();
                    // 메세지 삭제
                    deleteMessageTest();
                    System.out.println("| 1) 메세지 작성 | 2) 단건 조회 | 3) 다건 조회 | 4) 메세지 수정 | 5) 메세지 삭제 | 0) 뒤로가기");;
                    break;
                case "0":
                    System.out.println("메세지 테스트 종료...");
                    loop = false;
                    break;
                default:
                    System.out.println("잘못 입력하였습니다.");
            }
        }
    }
    private static void createMessageTest() {
        System.out.println("메세지 등록");
        System.out.println("입력 : ");
        String message = sc.nextLine();
        if(!message.equals("0")){
            try{
                messageId = messageService.createMessage(userId, channelId, message);
                System.out.print("메세지 등록 완료 : ");
                System.out.println(userService.findUserById(userId).getUsername()+" : "+messageService.findMessageByMessageId(messageId).getMessage());
            } catch (Exception e) {
                System.out.println("잘못된 입력");
            }
        }
    }
    private static void  singleMessageSearchTest() {
        System.out.println("단건 조회 - 조회할 메세지 아이디를 입력해주세요( 0) 뒤로가기)");
        Channel1 channel = channelService.findChannelById(channelId);
        channel.getMessages().forEach(message -> System.out.println(message.getMessage()+" : "+message.getId()));
        System.out.println("입력 : ");
        String choice = sc.nextLine();
        if(!choice.equals("0")) {
            try{
                UUID inputMessageId = UUID.fromString(choice);
                Message1 selectedMessage = messageService.findMessageByMessageId(inputMessageId);
                System.out.println(selectedMessage.getId() + " : " + selectedMessage.getMessage());
                // 글로벌 변수에 저장
                messageId = UUID.fromString(choice);
            } catch (Exception e) {
                System.out.println("잘못된 입력");
            }
        }
    }

    private static void updateMessageTitleTest() {
        System.out.println("메세지 수정( 0) 뒤로가기)");
        String message = sc.nextLine();
        if (!message.equals("0")) {
            try{

                System.out.println("before : " + messageId + " : " + messageService.findMessageByMessageId(messageId).getMessage());
                messageService.updateMessage(messageId, message);
                System.out.println("after : " + messageId + " : " + messageService.findMessageByMessageId(messageId).getMessage());
            } catch (Exception e) {
                System.out.println("wrong input");
            }
        }
    }

    private static void multipleMessageSearchTest() {
        System.out.println("다건 조회");
        messageService.findAllMessages().forEach(message -> System.out.println((message.getId() + " : " + message.getMessage())));
    }

    private static void deleteMessageTest() {
        messageService.deleteMessageById(messageId);
        System.out.println("내 채널내 전체 메세지");
        Channel1 channel = channelService.findChannelById(channelId);
        List<Message1> messages = channel.getMessages();
        for (Message1 message : messages) {
            System.out.println(message.getId() + message.getMessage());
        }
    }

    private static void channelTest(){
        boolean loop = true;
        System.out.println("| 1) 채널 등록 | 2) 단건 조회 | 3) 다건 조회 | 4) 채널명 수정 | 5) 채널 삭제 | 0) 종료");
        while (loop) {
            System.out.println("입력: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    createChannelTest();
                    System.out.println("| 1) 채널 등록 | 2) 단건 조회 | 3) 다건 조회 | 4) 채널명 수정 | 5) 채널 삭제 | 0) 종료");
                    break;
                case "2":
                    singleChannelSearchTest();
                    System.out.println("| 1) 채널 등록 | 2) 단건 조회 | 3) 다건 조회 | 4) 채널명 수정 | 5) 채널 삭제 | 0) 종료");
                    break;
                case "3":
                    multipleChannelsSearchTest();
                    System.out.println("| 1) 채널 등록 | 2) 단건 조회 | 3) 다건 조회 | 4) 채널명 수정 | 5) 채널 삭제 | 0) 종료");
                    break;
                case "4":
                    channelNameUpdateTest();
                    System.out.println("| 1) 채널 등록 | 2) 단건 조회 | 3) 다건 조회 | 4) 채널명 수정 | 5) 채널 삭제 | 0) 종료");
                    break;
                case "5":
                    deleteChannelTest();
                    System.out.println("| 1) 채널 등록 | 2) 단건 조회 | 3) 다건 조회 | 4) 채널명 수정 | 5) 채널 삭제 | 0) 종료");
                    break;
                case "0":
                    System.out.println("채널 테스트 종료...");
                    loop = false;
                    break;
                default:
                    System.out.println("잘못 입력하였습니다.");
            }
        }
    }
    private static void createChannelTest(){
        System.out.println("채널 등록");
        UUID channelId = channelService.createChannel(userId);
        System.out.print("채널 등록 완료 : ");
        System.out.println(channelService.findChannelById(channelId).getTitle());
    }
    private static void singleChannelSearchTest(){
        System.out.println("단건 조회 - 조회할 채널 아이디를 입력해주세요( 0) 뒤로가기)");
        userService.findChannelIdsInId(userId).forEach(channelId -> System.out.println("채널ID : "+channelId));
        System.out.println("입력 : ");
        String choice = sc.nextLine();
        if(!choice.equals("0")) {
            try{
                UUID inputChannelId = UUID.fromString(choice);
                Channel1 selectedChannel = channelService.findChannelById(inputChannelId);
                System.out.println(selectedChannel.getTitle() + " : " + selectedChannel.getId());
                // 글로벌 변수에 저장
                channelId = UUID.fromString(choice);
            } catch (Exception e) {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private static void multipleChannelsSearchTest(){
        System.out.println("다건 조회");
        channelService.findAllChannel().forEach(channel -> System.out.println(channel.getTitle() + " : " + channel.getId()));
    }

    private static void channelNameUpdateTest(){
        System.out.println("채널명 수정( 0) 뒤로가기)");
        userService.findChannelIdsInId(userId).forEach(channelId -> System.out.println("채널ID : "+channelId));
        System.out.println("입력 : ");
        String choice = sc.nextLine();
        if (!choice.equals("0")) {
            try {
                UUID inputChannelId = UUID.fromString(choice);
                Channel1 selectedChannel = channelService.findChannelById(inputChannelId);
                System.out.println("새 채널명 입력: ");
                String newTitle = sc.nextLine();
                System.out.println("before : "+selectedChannel.getTitle() + " : " + selectedChannel.getId());
                channelService.updateChannelName(inputChannelId,newTitle);
                selectedChannel = channelService.findChannelById(inputChannelId);
                System.out.println("after : " + selectedChannel.getTitle() + " : " + selectedChannel.getId());
            } catch (Exception e) {
                System.out.println("잘못된 입력입니다.");
            }

        }

    }
    private static void deleteChannelTest(){
        System.out.println("채널 삭제( 0) 뒤로가기)");
        userService.findChannelIdsInId(userId).forEach(channelId -> System.out.println("채널ID : "+channelId));
        System.out.println("입력 : ");
        String choice = sc.nextLine();
        if(!choice.equals("0")){
            try{
                UUID inputChannelId = UUID.fromString(choice);
                channelService.deleteChannel(inputChannelId);

                Channel1 selectedChannel = channelService.findChannelById(inputChannelId);
                System.out.println("result : "+ selectedChannel);
            } catch (Exception e) {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

}
