package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(userService, channelService);


        //*** User 클래스 ***
        System.out.println("--------- User 클래스 ---------");

        // 1. 사용자 생성
        List<String> names = List.of("민지", "수연", "태현", "재민");
        List<User> users = names.stream()
                .map(userService::createUser)
                .toList();
        User testUser = userService.createUser("민지"); // 중복 이름 예외 처리

        // 2. 모든 사용자 조회
        System.out.println("\n[User] 모든 사용자 조회");
        userService.getAllUsers().forEach(System.out::println);
        //users.forEach(System.out::println);

        // 3. 사용자 조회
        System.out.println("\n[User] 단일 사용자 조회 (수연, 태현)");
        System.out.println(userService.getUser(users.get(1).getId())); // getUser("수연")
        System.out.println(userService.getUser(users.get(2).getId())); // getUser("태현")

        // 4. 사용자 수정
        System.out.println("\n[User] 수정된 사용자 정보 (민지, 수연)");
        userService.updateUser(users.get(0).getId(), "김민지"); // 민지 → 김민지
        userService.updateUser(users.get(1).getId(), "이수연"); // 수연 → 이수연
        System.out.println(userService.getUser(users.get(0).getId()));
        System.out.println(userService.getUser(users.get(1).getId()));

        // 5. 사용자 삭제
        userService.deleteUser(users.get(3).getId()); // 재민 삭제
        System.out.println("\n[User] 삭제 후 전체 사용자 조회 (재민)");
        userService.getAllUsers().forEach(System.out::println);



        //*** Channel 클래스 ***
        System.out.println("\n--------- Channel 클래스 ---------");

        // 1. 채널 생성
        List<String> channelNames = List.of("코드잇", "스프린트", "부트캠프");
        List<Channel> channels = channelNames.stream()
                .map(channelService::createChannel)
                .filter(Objects::nonNull)
                .toList();
        Channel testChannel = channelService.createChannel("코드잇"); // 중복 채널명 예외 처리

        // 2. 모든 채널 조회
        System.out.println("\n[Channel] 모든 채널 조회");
        channelService.getAllChannels().forEach(System.out::println);

        // 3. 채널 조회
        System.out.println("\n[Channel] 단일 채널 조회 (코드잇, 스프린트)");
        System.out.println(channelService.getChannel(channels.get(0).getId()));
        System.out.println(channelService.getChannel(channels.get(1).getId()));

        // 4. 채널 수정
        System.out.println("\n[Channel] 수정된 채널 정보 (코드잇, 스프린트)");
        channelService.updateChannel(channels.get(0).getId(), "Codeit");
        channelService.updateChannel(channels.get(1).getId(), "Sprint");
        System.out.println(channelService.getChannel(channels.get(0).getId()));
        System.out.println(channelService.getChannel(channels.get(1).getId()));

        // 5. 채널 삭제
        System.out.println("\n[Channel] 삭제 후 전체 채널 조회 (Sprint, 부트캠프)");
        channelService.deleteChannel(channels.get(1).getId()); // Sprint 삭제
        channelService.getAllChannels().forEach(System.out::println);

        // 6. 채널 접속
        System.out.println("\n[Channel] 채널 접속 (코드잇 - 민지, 수연, 태현)");
        channelService.joinChannel(users.get(0).getId(), channels.get(0).getId());
        channelService.joinChannel(users.get(1).getId(), channels.get(0).getId());
        channelService.joinChannel(users.get(2).getId(), channels.get(0).getId());

        System.out.println("\n[Channel] 채널 접속 후 조회 ");
        channelService.getAllChannels().forEach(System.out::println);

        // 7. 채널 접속 해제
        System.out.println("\n[Channel] 채널 접속 해제 후 조회 (코드잇 - 수연)");
        channelService.leaveChannel(users.get(1).getId(), channels.get(0).getId());
        channelService.getAllChannels().forEach(System.out::println);



        //*** Message 클래스 ***
        System.out.println("\n--------- Message 클래스 ---------");

        // 1. 메시지 생성
        System.out.println("[Message] 유효성 검사");
        List<Message> messages = Stream.of(
                messageService.createMessage(users.get(0).getId(), channels.get(0).getId(), "안녕하세요!"),
                messageService.createMessage(users.get(2).getId(), channels.get(0).getId(), "반갑습니다!"),
                        messageService.createMessage(users.get(2).getId(), channels.get(0).getId(), "코드잇 채널입니다."),
                messageService.createMessage(users.get(3).getId(), channels.get(0).getId(), "사용자 예외 처리 테스트"), // 존재하지 않는 사용자 예외 발생 !
                messageService.createMessage(users.get(0).getId(), channels.get(1).getId(), "서버 예외 처리 테스트"), // 존재하지 않는 서버 예외 발생
                messageService.createMessage(users.get(1).getId(), channels.get(0).getId(), "채널 접속 예외 처리 테스트") // 접속하지 않는 채널 예외 발생
        )
                .filter(Objects::nonNull)
                .toList();

        // 2. 모든 메시지 조회
        System.out.println("\n[Message] 모든 메시지 조회");
        messageService.getAllMessages().forEach(System.out::println);

        // 3. 메시지 조회
        System.out.println("\n[Message] 단일 메시지 조회");
        System.out.println(messageService.getMessage(messages.get(0).getId()));
        System.out.println(messageService.getMessage(messages.get(1).getId()));

        // 4. 채널의 메시지 조회
        System.out.println("\n[Message] 채널1의 메시지 조회");
        messageService.getMessagesByChannel(channels.get(0).getId()).forEach(System.out::println);

        // 5. 메시지 수정
        System.out.println("\n[Message] 메시지 수정 (1번 메시지)");
        messageService.updateMessage(messages.get(0).getId(), "Hello!");
        System.out.println(messageService.getMessage(messages.get(0).getId()));

        // 6. 메시지 삭제
        System.out.println("\n[Message] 삭제 후 전체 메시지 조회");
        messageService.deleteMessage(messages.get(1).getId()); // msg2 삭제
        messageService.getAllMessages().forEach(System.out::println);
    }
}
