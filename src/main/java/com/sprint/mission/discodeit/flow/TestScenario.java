package com.sprint.mission.discodeit.flow;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.dto.entity.Message;
import com.sprint.mission.discodeit.dto.entity.User;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestScenario {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final ReadStatusService readStatusService;
    private final BinaryContentService binaryContentService;
    private final AuthService authService;
    private final ChannelService channelService;
    private final MessageService messageService;

    private List<User> users;
    private List<Channel> channels;
    private List<Message> messages;

    public TestScenario(
            UserService userService,
            UserStatusService userStatusService,
            ReadStatusService readStatusService,
            BinaryContentService binaryContentService,
            AuthService authService,
            ChannelService channelService,
            MessageService messageService
    ) {
        this.userService    = userService;
        this.userStatusService = userStatusService;
        this.readStatusService = readStatusService;
        this.binaryContentService = binaryContentService;
        this.authService = authService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    public void run() {
        testUserServices();
        testChannelServices();
//        testMessageServices();
    }

    private void testUserServices() {
        createUser();
        getAllUsers();
        getUser();
        updateUser();
        deleteUser();
    }

    private void testChannelServices() {
        createChannel();
        getAllChannels();
        getChannel();
        updateChannel();
        deleteChannel();
        joinChannel();
        leaveChannel();
    }

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

        List<UserCreateRequest> createDTOs = List.of(
                new UserCreateRequest("Jane", "jane@google.com", "1234"),
                new UserCreateRequest("Tom",  "tom@naver.com",  "2345"),
                new UserCreateRequest("Kate", "kate@daum.com", "3456")
        );

        List<BinaryContentCreateRequest> imageDTOs = Arrays.asList( //List.of()로는 null 값을 담을 수 없음
                new BinaryContentCreateRequest("Jane.png", "/data/binaryContents/Jane.png"),
                null,
                new BinaryContentCreateRequest("Kate.jpg", "/data/binaryContents/Kate.jpg")
        );

        users = IntStream.range(0, createDTOs.size())
                .mapToObj(i -> {
                    UserCreateRequest userDto = createDTOs.get(i);
                    BinaryContentCreateRequest imgDto = imageDTOs.get(i);

                    try {
                        User created = userService.createUser(userDto, imgDto);
                        return created;
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
        System.out.println("\n[User] 사용자 프로필 이미지 추가/수정 후 조회");

        List<BinaryContentCreateRequest> imageDTOs = List.of(
                new BinaryContentCreateRequest("EditJane.png", "/data/binaryContents/attachments"),
                new BinaryContentCreateRequest("AddTomProfile.jpg", "/data/binaryContents/attachments")
        );

        List<UserUpdateRequest> updateDTOs = List.of(
                new UserUpdateRequest(users.get(0).getId(), imageDTOs.get(0)),
                new UserUpdateRequest(users.get(1).getId(), imageDTOs.get(1))
        );

        updateDTOs.stream()
                .map(userService::updateUserProfileImage)
                .forEach(System.out::println);
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
                        Channel created = channelService.createChannel(name);
                        return channelService.getChannel(created.getId());
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
        System.out.println("\n[Message] 메시지 삭제 후 전체 메시지 조회");
        messageService.deleteMessage(messages.get(1).getId());
        messageService.getAllMessages().forEach(System.out::println);
    }
}
