package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Component
public class TestScenario {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final ReadStatusService readStatusService;
    private final BinaryContentService binaryContentService;
    private final AuthService authService;
    private final ChannelService channelService;
    private final MessageService messageService;

    private List<User> users;
    private List<Channel> publicChannels;
    private List<Channel> privateChannels;
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
        testMessageServices();
    }

    private void testUserServices() {
        createUser();
        getAllUsers();
        getUser();
        deleteUser();
    }

    private void testChannelServices() {
        createPublicChannel();
        createPrivateChannel();
        getChannelsByUserId();
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
                new BinaryContentCreateRequest("first", "first.jpg"),
                null,
                new BinaryContentCreateRequest("third", "third.jpg")
        );

        users = IntStream.range(0, createDTOs.size())
                .mapToObj(i -> {
                    UserCreateRequest userDto = createDTOs.get(i);
                    BinaryContentCreateRequest imgDto = imageDTOs.get(i);

                    try {
                        return userService.createUser(userDto, imgDto);
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

    private void deleteUser() {
        System.out.println("\n[User] 삭제 후 전체 사용자 조회 (Kate)");
        userService.deleteUser(users.get(2).getId());
        userService.getAllUsers().forEach(System.out::println);
    }

    private void createPublicChannel() {
        System.out.println("\n[Channel] 유효성 검사 후 채널 생성");

        List<PublicChannelCreateRequest> publicDTOs = List.of(
                new PublicChannelCreateRequest("ch1", "public channel1"),
                new PublicChannelCreateRequest("ch2", "public channel2"),
                new PublicChannelCreateRequest("ch3", "public channel3"),
                new PublicChannelCreateRequest("ch2", "이름 중복 검사")
        );

        publicChannels = publicDTOs.stream()
                .map(dto -> {
                    try {
                        return channelService.createPublicChannel(dto);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void createPrivateChannel() {
        List<UUID> membersForChA = List.of(users.get(0).getId(), users.get(1).getId());
        List<UUID> membersForChB = List.of(users.get(0).getId());

        List<PrivateChannelCreateRequest> privateDTOs = List.of(
                new PrivateChannelCreateRequest(new HashSet<>(membersForChA)),
                new PrivateChannelCreateRequest(new HashSet<>(membersForChB))
        );

        privateChannels = privateDTOs.stream()
                .map(dto -> {
                    try {
                        return channelService.createPrivateChannel(dto);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void getChannelsByUserId() {
        System.out.println("\n[Channel] User가 포함된 모든 채널 조회");
        channelService.getAllChannelsByUserId(users.get(0).getId()).forEach(System.out::println);
    }

    private void getChannel() {
        System.out.println("\n[Channel] 단일 채널 조회");
        System.out.println(channelService.getChannel(publicChannels.get(0).getId()));
        System.out.println(channelService.getChannel(privateChannels.get(0).getId()));
    }

    private void updateChannel() {
        System.out.println("\n[Channel] 수정된 채널 정보 (ch1, ch2)");

        List<PublicChannelUpdateRequest> updateDTOs = List.of(
                new PublicChannelUpdateRequest(publicChannels.get(0).getId(), "channel1"),
                new PublicChannelUpdateRequest(publicChannels.get(1).getId(), "channel2")
        );

        updateDTOs.forEach(channelService::updateChannel);
    }

    private void deleteChannel() {
        System.out.println("\n[Channel] 채널 삭제 (chB)");
        channelService.deleteChannel(privateChannels.get(1).getId());
    }

    private void joinChannel() {
        System.out.println("\n[Channel] 채널 접속 후 조회");
        channelService.joinChannel(users.get(0).getId(), publicChannels.get(0).getId());
        channelService.joinChannel(users.get(1).getId(), publicChannels.get(0).getId());
        channelService.joinChannel(users.get(0).getId(), publicChannels.get(1).getId());
        channelService.joinChannel(users.get(1).getId(), publicChannels.get(1).getId());
    }

    private void leaveChannel() {
        System.out.println("\n[Channel] 채널 탈퇴 후 조회 (ch2)");
        channelService.leaveChannel(users.get(1).getId(), publicChannels.get(1).getId());
    }

    private void createMessage() {
        System.out.println("\n[Message] 유효성 검사 후 메시지 생성");

        List<MessageCreateRequest> requestDTOs = List.of(
                new MessageCreateRequest(
                        users.get(0).getId(),
                        publicChannels.get(0).getId(),
                        "안녕하세요!",
                        null
                ),
                // 파일 등록 로직 수정 필요
                new MessageCreateRequest(
                        users.get(1).getId(),
                        publicChannels.get(0).getId(),
                        "파일 첨부 테스트입니다",
                        List.of(
                                new BinaryContentCreateRequest("abc.txt", "first.jpg"),
                                new BinaryContentCreateRequest("doc.pdf", "second.jpg")
                        )
                ),
                new MessageCreateRequest(
                        users.get(0).getId(),
                        publicChannels.get(1).getId(),
                        "메시지 삭제 테스트 용",
                        null
                ),
                new MessageCreateRequest(
                        UUID.randomUUID(),
                        publicChannels.get(0).getId(),
                        "사용자 예외 처리 테스트",
                        null
                )
        );

        messages = requestDTOs.stream()
                .map(dto -> {
                    try {
                        return messageService.createMessage(dto);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void getAllMessages() {
        System.out.println("\n[Message] 모든 메시지 조회");
//        messageService.getAllMessages().forEach(System.out::println);

        publicChannels.forEach(channel ->
            messageService.getMessagesByChannel(channel.getId())
                    .forEach(System.out::println)
        );

        privateChannels.forEach(channel ->
                messageService.getMessagesByChannel(channel.getId())
                        .forEach(System.out::println)
        );
    }

    private void getMessage() {
        System.out.println("\n[Message] 단일 메시지 조회");
        System.out.println(messageService.getMessage(messages.get(0).getId()));
        System.out.println(messageService.getMessage(messages.get(1).getId()));
    }

    private void updateMessage() {
        System.out.println("\n[Message] 메시지 수정 (1번 메시지)");
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(messages.get(0).getId(), "Hello!");
        messageService.updateMessage(messageUpdateRequest);
        System.out.println(messageService.getMessage(messages.get(0).getId()));
    }

    private void deleteMessage() {
        System.out.println("\n[Message] 메시지 삭제 후 전체 메시지 조회");
        messageService.deleteMessage(messages.get(2).getId());
        messageService.getAllMessages().forEach(System.out::println);
    }
}
