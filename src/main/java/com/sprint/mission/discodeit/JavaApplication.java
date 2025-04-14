package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;          // 도메인 클래스들: User, Channel, Message
import com.sprint.mission.discodeit.service.*;         // 서비스 인터페이스들
import com.sprint.mission.discodeit.jcf.*;             // 실제 구현 클래스들

import java.util.Comparator;


public class JavaApplication {

    public static void main(String[] args) {

        // 서비스 생성 (팩토리 사용)
        UserService userService = ServiceFactory.createUserService();
        ChannelService channelService = ServiceFactory.createChannelService();
        MessageService messageService = ServiceFactory.createMessageService(userService, channelService);


        System.out.println("\n==============[유저]==============");
        //============================[UserServiceTset]============================

        //유저 생성
        User user1 = new User("테스트-01");
        User user2 = new User("테스트-02");
        User user3 = new User("테스트-03");
        User user4 = new User("테스트-04");
        User user5 = new User("테스트-05");

        //등록
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        userService.create(user4);
        userService.create(user5);

        //전체 조회
        System.out.println("[전체 유저 목록]");
        userService.getAll().forEach(user ->
                System.out.println(user.getName()+ " (ID: " + user.getId() + ")"));
                System.out.println("-----------------------------");

        //단일 조회
        System.out.println("[단일 유저 조회]");
        User found = userService.getById(user1.getId());
        System.out.println("조회된 유저 : " + found.getName()+ " (ID: " + found.getId() + ")");
        System.out.println("-----------------------------");

        //업데이트
        user1.updateName("테스트-01 (수정됨)");
        userService.update(user1);

        System.out.println("[수정된 유저]\n" + userService.getById(user1.getId()).getName());
        System.out.println("-----------------------------");

        //삭제
        userService.delete(user5.getId());

        System.out.println("[삭제 후 전체 유저 목록]");
        userService.getAll().forEach(user -> System.out.println(user.getName()));
        System.out.println("-----------------------------");

        //============================[ChannelServiceTest]============================

        //채널 등록
        Channel ch1 = new Channel("테스트채널-01");
        Channel ch2 = new Channel("테스트채널-02");
        Channel ch3 = new Channel("테스트채널-03");
        Channel ch4 = new Channel("테스트채널-04");

        //등록
        channelService.create(ch1);
        channelService.create(ch2);
        channelService.create(ch3);
        channelService.create(ch4);

        System.out.println("\n==============채널==============");
        // 전체 출력
        System.out.println("[전체 채널 목록]");
        channelService.getAll().forEach(channel ->
                System.out.println("- " + channel.getName() + " (ID : " + channel.getId() + ")"));
                System.out.println("-----------------------------");

        //단일조회
        System.out.println("[단일 채널 조회]");
        Channel foundChannel = channelService.getById(ch2.getId());
        System.out.println("조회된 채널 : " + foundChannel.getName());
        System.out.println("-----------------------------");

        //수정
        ch2.updateName("테스트채널-02 (수정됨)");
        channelService.update(ch2);

        System.out.println("[수정 후 조회]");
        System.out.println("- " + channelService.getById(ch2.getId()).getName());
        System.out.println("-----------------------------");

        //t삭제
        channelService.delete(ch3.getId());

        System.out.println("[삭제 후 채널 목록]");
        channelService.getAll().forEach(channel ->
                System.out.println("- " + channel.getName()));
                System.out.println("-----------------------------");

        //============================[MessageServiceTest]============================

        Message msg1 = new Message(user1.getId(), ch1.getId(), "테스트메시지-01");
        Message msg2 = new Message(user2.getId(), ch2.getId(), "테스트메시지-02");

        messageService.create(msg1);
        messageService.create(msg2);

        System.out.println("\n==============메시지==============");
        //전체 메세지 출력
        System.out.println("[전체 메시지 목록]");
        messageService.getAll().forEach(msg -> {
            String userName = userService.getById(msg.getUserId()).getName();
            String channelName = channelService.getById(msg.getChannelId()).getName();

            System.out.println("내용     : " + msg.getContent());
            System.out.println("작성자   : " + userName + " (ID: " + msg.getUserId() + ")");
            System.out.println("채널명   : " + channelName + " (ID: " + msg.getChannelId() + ")");
            System.out.println("-----------------------------");
        });

        //단일 메세지 조회
        System.out.println("[단일 메시지 조회]");
        Message foundMsg = messageService.getById(msg1.getId());
        System.out.println("조회된 메시지 : " + foundMsg.getContent());
        System.out.println("-----------------------------");

        //수정
        msg1.updateContent("테스트메시지-01 (수정됨)");
        messageService.update(msg1);

        System.out.println("[수정된 메시지 확인]");
        System.out.println(messageService.getById(msg1.getId()).getContent());
        System.out.println("-----------------------------");

        //삭제
        messageService.delete(msg2.getId());

        System.out.println("[삭제 후 메시지 목록]");
        messageService.getAll().forEach(msg ->
                System.out.println(msg.getContent()));
                System.out.println("-----------------------------");

        System.out.println("[최신순 메시지 목록]");
        messageService.getAll().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed()) // 최신순 정렬
                .forEach(msg -> {
                    String userName = userService.getById(msg.getUserId()).getName();
                    String channelName = channelService.getById(msg.getChannelId()).getName();
                    System.out.println("내용     : " + msg.getContent());
                    System.out.println("작성자   : " + userName);
                    System.out.println("채널명   : " + channelName);
                    System.out.println("-----------------------------");
                });

        System.out.println("[채널 ch1의 메시지 목록]");
        messageService.getAll().stream()
                .filter(msg -> msg.getChannelId().equals(ch1.getId()))
                .forEach(msg -> {
                    String userName = userService.getById(msg.getUserId()).getName();
                    System.out.println("내용     : " + msg.getContent());
                    System.out.println("작성자   : " + userName);
                    System.out.println("-----------------------------");
                });


    }
}
