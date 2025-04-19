package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class JavaApplication2 {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    private List<User> users;
    private List<Channel> channels;
    private List<Message> messages;

    public JavaApplication2() {
        userService = new FileUserService();
        channelService = new FileChannelService(userService);
        messageService = new FileMessageService(userService, channelService);
    }

    public void run() {
        testUserServices();
        testChannelServices();
        testMessageServices();
    }

    /**
     * UserService를 테스트하는 로직
     * 1. user 생성 후 전체 user 조회
     * 2. 단일 user 조회
     * 3. user 정보 수정
     * 4. user 삭제
     */
    private void testUserServices() {
        createUser();
        getAllUsers();
        getUser();
        updateUser();
        deleteUser();
    }

    /**
     * ChannelService를 테스트하는 로직
     * 1. channel 생성 후 전체 channel 조회
     * 2. 단일 channel 조회
     * 3. channel 정보 수정
     * 4. channel 삭제
     * 5. channel 접속
     * 6. channel 탈퇴
     */
    private void testChannelServices() {
        createChannel();
        getAllChannels();
        getChannel();
        updateChannel();
        deleteChannel();
        joinChannel();
        leaveChannel();
    }

    /**
     * MessageService를 테스트하는 로직
     * 1. message 생성 후 전체 message 조회
     * 2. 단일 message 조회
     * 3. channel별 message 조회
     * 4. message 수정
     * 5. message 삭제
     */
    private void testMessageServices() {
        createMessage();
        getAllMessages();
        getMessage();
        getChannelMessage();
        updateMessage();
        deleteMessage();
    }

    private void createUser() {
        System.out.println("\n[User] 유효성 검사 후 사용자 생성");
        List<String> names = List.of("Jane", "Tom", "Kate", "Kate");
        users = names.stream()
                .map(name -> {
                    try {
                        return userService.createUser(name);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void getAllUsers() {
        System.out.println("\n[User] 모든 사용자 조회");
        userService.getAllUsers().forEach(System.out::println);
    }

    private void getUser() {
        System.out.println("\n[User] 단일 사용자 조회");
        System.out.println(userService.getUser(users.get(0).getId()));
        System.out.println(userService.getUser(users.get(1).getId()));
    }

    private void updateUser() {
        System.out.println("\n[User] 수정된 사용자 정보");
        userService.updateUser(users.get(0).getId(), "John");
        userService.updateUser(users.get(1).getId(), "Ted");
        System.out.println(userService.getUser(users.get(0).getId()));
        System.out.println(userService.getUser(users.get(1).getId()));
    }

    private void deleteUser() {
        System.out.println("\n[User] 삭제 후 전체 사용자 조회 (Kate)");
        userService.deleteUser(users.get(2).getId());
        userService.getAllUsers().forEach(System.out::println);
    }

    private void createChannel() {
        System.out.println("\n[Channel] 유효성 검사 후 채널 생성");
        List<String> channelNames = List.of("ch1", "ch2", "ch3", "ch2");
        channels = channelNames.stream()
                .map(name -> {
                    try {
                        return channelService.createChannel(name);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void getAllChannels() {
        System.out.println("\n[Channel] 모든 채널 조회");
        channelService.getAllChannels().forEach(System.out::println);
    }

    private void getChannel() {
        System.out.println("\n[Channel] 단일 채널 조회");
        System.out.println(channelService.getChannel(channels.get(0).getId()));
        System.out.println(channelService.getChannel(channels.get(1).getId()));
    }

    private void updateChannel() {
        System.out.println("\n[Channel] 수정된 채널 정보 (ch1, ch2)");
        channelService.updateChannel(channels.get(0).getId(), "channel1");
        channelService.updateChannel(channels.get(1).getId(), "channel2");
        System.out.println(channelService.getChannel(channels.get(0).getId()));
        System.out.println(channelService.getChannel(channels.get(1).getId()));
    }

    private void deleteChannel() {
        System.out.println("\n[Channel] 삭제 후 전체 채널 조회 (ch3)");
        channelService.deleteChannel(channels.get(2).getId());
        channelService.getAllChannels().forEach(System.out::println);
    }

    private void joinChannel() {
        System.out.println("\n[Channel] 채널 접속");
        channelService.joinChannel(users.get(0).getId(), channels.get(0).getId());
        channelService.joinChannel(users.get(1).getId(), channels.get(0).getId());
        channelService.joinChannel(users.get(1).getId(), channels.get(1).getId());

        System.out.println("\n[Channel] 채널 접속 후 조회 ");
        channelService.getAllChannels().forEach(System.out::println);
    }

    private void leaveChannel() {
        System.out.println("\n[Channel] 채널 탈퇴");
        channelService.leaveChannel(users.get(1).getId(), channels.get(1).getId());
        channelService.getAllChannels().forEach(System.out::println);

        System.out.println("\n[Channel] 채널 탈퇴 후 조회 ");
        channelService.getAllChannels().forEach(System.out::println);
    }

    private void createMessage() {
        System.out.println("\n[Message] 유효성 검사 후 메시지 생성");
        messages = Stream.of(
                        messageService.createMessage(users.get(0).getId(), channels.get(0).getId(), "안녕하세요!"),
                        messageService.createMessage(users.get(1).getId(), channels.get(0).getId(), "1번 채널입니다."),
                        messageService.createMessage(users.get(2).getId(), channels.get(0).getId(), "사용자 예외 처리 테스트"), // 존재하지 않는 사용자 예외 발생 !
                        messageService.createMessage(users.get(0).getId(), channels.get(2).getId(), "서버 예외 처리 테스트"), // 존재하지 않는 서버 예외 발생
                        messageService.createMessage(users.get(0).getId(), channels.get(1).getId(), "채널 접속 예외 처리 테스트") // 접속하지 않는 채널 예외 발생
                )
                .filter(Objects::nonNull)
                .toList();
    }

    private void getAllMessages() {
        System.out.println("\n[Message] 모든 메시지 조회");
        messageService.getAllMessages().forEach(System.out::println);
    }

    private void getMessage() {
        System.out.println("\n[Message] 단일 메시지 조회");
        System.out.println(messageService.getMessage(messages.get(0).getId()));
        System.out.println(messageService.getMessage(messages.get(1).getId()));
    }

    private void getChannelMessage() {
        System.out.println("\n[Message] channel1의 메시지 조회");
        messageService.getMessagesByChannel(channels.get(0).getId()).forEach(System.out::println);
    }

    private void updateMessage() {
        System.out.println("\n[Message] 메시지 수정 (1번 메시지)");
        messageService.updateMessage(messages.get(0).getId(), "Hello!");
        System.out.println(messageService.getMessage(messages.get(0).getId()));
    }

    private void deleteMessage() {
        System.out.println("\n[Message] 삭제 후 전체 메시지 조회");
        messageService.deleteMessage(messages.get(1).getId());
        messageService.getAllMessages().forEach(System.out::println);
    }

    public static void main(String[] args) {
        new JavaApplication2().run();
    }
}