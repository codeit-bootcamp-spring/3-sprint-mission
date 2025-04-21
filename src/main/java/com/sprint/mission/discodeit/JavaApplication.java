package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.file.*;

import java.util.Comparator;


public class JavaApplication {

    public static void main(String[] args) {

        // 서비스 생성
        // 교체 후 (직접 생성)
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();


        runUserServiceTest(userService);
        runChannelServiceTest(channelService);
        runMessageServiceTest(messageService, userService, channelService);

    }

    private static void runUserServiceTest(UserService userService) {
        System.out.println("\n============== [유저 서비스 테스트 시작] ==============");

        // Given - 유저 생성
        User user1 = new User("테스트유저");
        User user2 = new User("테스트유저2");

        // When - 유저 등록
        userService.create(user1);
        userService.create(user2);

        // Then - 전체 유저 목록 조회
        System.out.println("\n[전체 유저 목록]");
        userService.getAll().forEach(System.out::println);

        // When - 단일 유저 조회
        User foundUser = userService.getById(user1.getId());

        // Then - 단일 유저 정보 출력
        System.out.println("\n[단일 유저 조회]");
        System.out.println(foundUser);

        // When - 유저 업데이트
        user1.updateName("수정된 유저");
        userService.update(user1);

        // Then - 수정된 결과 확인
        System.out.println("\n[수정된 유저]");
        System.out.println(userService.getById(user1.getId()));

        // When - 유저 삭제
        userService.delete(user2.getId());

        // Then - 삭제 후 조회
        System.out.println("\n[삭제된 유저 조회 시]");
        System.out.println(userService.getById(user2.getId()));  // null 출력 예상

        System.out.println("============== [유저 서비스 테스트 종료] ==============\n");
    }

    private static void runChannelServiceTest(ChannelService channelService) {
        System.out.println("\n============== [채널 서비스 테스트 시작] ==============");

        // Given - 채널 생성
        Channel ch1 = new Channel("테스트채널-01");
        Channel ch2 = new Channel("테스트채널-02");
        Channel ch3 = new Channel("테스트채널-03");
        Channel ch4 = new Channel("테스트채널-04");

        // When - 채널 등록
        channelService.create(ch1);
        channelService.create(ch2);
        channelService.create(ch3);
        channelService.create(ch4);

        // Then - 전체 채널 목록 출력
        System.out.println("\n[전체 채널 목록]");
        channelService.getAll().forEach(channel ->
                System.out.println("- " + channel.getName() + " (ID : " + channel.getId() + ")"));
        System.out.println("-----------------------------");

        // When - 단일 채널 조회
        Channel foundChannel = channelService.getById(ch2.getId());

        // Then - 단일 채널 정보 출력
        System.out.println("[단일 채널 조회]");
        System.out.println("조회된 채널 : " + foundChannel.getName());
        System.out.println("-----------------------------");

        // When - 채널 이름 수정
        ch2.updateName("테스트채널-02 (수정됨)");
        channelService.update(ch2);

        // Then - 수정 후 조회 결과 출력
        System.out.println("[수정 후 채널 조회]");
        System.out.println("- " + channelService.getById(ch2.getId()).getName());
        System.out.println("-----------------------------");

        // When - 채널 삭제
        channelService.delete(ch3.getId());

        // Then - 삭제 후 전체 목록 확인
        System.out.println("[삭제 후 채널 목록]");
        channelService.getAll().forEach(channel ->
                System.out.println("- " + channel.getName()));
        System.out.println("-----------------------------");

        System.out.println("============== [채널 서비스 테스트 종료] ==============\n");
    }
    private static void runMessageServiceTest(MessageService messageService, UserService userService,
                                              ChannelService channelService) {
        System.out.println("\n============== [메시지 서비스 테스트 시작] ==============");

        // Given - 메시지 테스트용 유저/채널 생성
        User user1 = new User("유저-01");
        User user2 = new User("유저-02");
        userService.create(user1);
        userService.create(user2);

        Channel ch1 = new Channel("채널-01");
        Channel ch2 = new Channel("채널-02");
        channelService.create(ch1);
        channelService.create(ch2);

        // Given - 메시지 2개 생성
        Message msg1 = new Message(user1.getId(), ch1.getId(), "테스트메시지-01");
        Message msg2 = new Message(user2.getId(), ch2.getId(), "테스트메시지-02");

        // When - 메시지 등록
        messageService.create(msg1);
        messageService.create(msg2);

        // Then - 전체 메시지 목록 출력
        System.out.println("\n[전체 메시지 목록]");
        messageService.getAll().forEach(msg -> {
            String userName = userService.getById(msg.getUserId()).getName();
            String channelName = channelService.getById(msg.getChannelId()).getName();

            System.out.println("내용     : " + msg.getContent());
            System.out.println("작성자   : " + userName + " (ID: " + msg.getUserId() + ")");
            System.out.println("채널명   : " + channelName + " (ID: " + msg.getChannelId() + ")");
            System.out.println("-----------------------------");
        });

        // When - 단일 메시지 조회
        Message foundMsg = messageService.getById(msg1.getId());

        // Then - 단일 메시지 정보 출력
        System.out.println("\n[단일 메시지 조회]");
        System.out.println("조회된 메시지 : " + foundMsg.getContent());
        System.out.println("-----------------------------");

        // When - 메시지 수정
        msg1.updateContent("테스트메시지-01 (수정됨)");
        messageService.update(msg1);

        // Then - 수정된 메시지 출력
        System.out.println("[수정된 메시지 확인]");
        System.out.println(messageService.getById(msg1.getId()).getContent());
        System.out.println("-----------------------------");

        // When - 메시지 삭제
        messageService.delete(msg2.getId());

        // Then - 삭제 후 전체 목록 출력
        System.out.println("[삭제 후 메시지 목록]");
        messageService.getAll().forEach(msg ->
                System.out.println(msg.getContent())
        );
        System.out.println("-----------------------------");

        // 최신순 정렬된 메시지 목록
        System.out.println("[최신순 메시지 목록]");
        messageService.getAll().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .forEach(msg -> {
                    String userName = userService.getById(msg.getUserId()).getName();
                    String channelName = channelService.getById(msg.getChannelId()).getName();
                    System.out.println("내용     : " + msg.getContent());
                    System.out.println("작성자   : " + userName);
                    System.out.println("채널명   : " + channelName);
                    System.out.println("-----------------------------");
                });

        // 특정 채널 메시지 목록 필터링
        System.out.println("[채널 " + ch1.getName() + "의 메시지 목록]");
        messageService.getAll().stream()
                .filter(msg -> msg.getChannelId().equals(ch1.getId()))
                .forEach(msg -> {
                    String userName = userService.getById(msg.getUserId()).getName();
                    System.out.println("내용     : " + msg.getContent());
                    System.out.println("작성자   : " + userName);
                    System.out.println("-----------------------------");
                });

        System.out.println("============== [메시지 서비스 테스트 종료] ==============");
    }


}

