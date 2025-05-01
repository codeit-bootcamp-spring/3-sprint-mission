//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
//import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
//import com.sprint.mission.discodeit.service.jcf.JCFUserService;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Stream;
//
//public class JavaApplication {
//    private final JCFUserService userService;
//    private final JCFChannelService channelService;
//    private final JCFMessageService messageService;
//
//    private List<User> users;
//    private List<Channel> channels;
//    private List<Message> messages;
//
//    public JavaApplication() {
//        userService = new JCFUserService();
//        channelService = new JCFChannelService(userService);
//        messageService = new JCFMessageService(channelService);
//    }
//
//    public void run() {
//        testUserServices();
//        testChannelServices();
//        testMessageServices();
//    }
//
//    private void testUserServices() {
//        createUser();
//        getAllUsers();
//        getUser();
//        updateUser();
//        deleteUser();
//    }
//
//    private void testChannelServices() {
//        createChannel();
//        getAllChannels();
//        getChannel();
//        updateChannel();
//        deleteChannel();
//        joinChannel();
//        leaveChannel();
//    }
//
//    private void testMessageServices() {
//        createMessage();
//        getAllMessages();
//        getMessage();
//        getChannelMessage();
//        updateMessage();
//        deleteMessage();
//    }
//
//    /**
//     * [User] 사용자 생성 테스트
//     *
//     * Given: 사용자 이름이 중복되지 않을 때
//     * When: 이름 값으로 사용자들을 생성하면
//     * Then: 사용자 리스트에 저장된다.
//     */
//    private void createUser() {
//        List<String> names = List.of("민지", "수연", "태현", "재민", "민지"); // 이름 중복
//        users = names.stream()
//                .map(name -> {
//                    try {
//                        return userService.createUser(name);
//                    } catch (IllegalArgumentException e) {
//                        System.out.println(e.getMessage());
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull) // null값 제외
//                .toList();
//    }
//
//    private void getAllUsers() {
//        System.out.println("\n[User] 모든 사용자 조회");
//        userService.getAllUsers().forEach(System.out::println);
//        //users.forEach(System.out::println);
//    }
//
//    private void getUser() {
//        System.out.println("\n[User] 단일 사용자 조회 (수연, 태현)");
//        System.out.println(userService.getUser(users.get(1).getId())); // getUser("수연")
//        System.out.println(userService.getUser(users.get(2).getId())); // getUser("태현")
//    }
//
//    private void updateUser() {
//        System.out.println("\n[User] 수정된 사용자 정보 (민지, 수연)");
//        userService.updateUser(users.get(0).getId(), "김민지"); // 민지 → 김민지
//        userService.updateUser(users.get(1).getId(), "이수연"); // 수연 → 이수연
//        System.out.println(userService.getUser(users.get(0).getId()));
//        System.out.println(userService.getUser(users.get(1).getId()));
//    }
//
//    private void deleteUser() {
//        userService.deleteUser(users.get(3).getId()); // 재민 삭제
//        System.out.println("\n[User] 삭제 후 전체 사용자 조회 (재민)");
//        userService.getAllUsers().forEach(System.out::println);
//    }
//
//    private void createChannel() {
//        List<String> channelNames = List.of("코드잇", "스프린트", "부트캠프", "코드잇"); //채널명 중복
//        channels = channelNames.stream()
//                .map(name -> {
//                    try {
//                        return channelService.createChannel(name);
//                    } catch (IllegalArgumentException e) {
//                        System.out.println(e.getMessage());
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .toList();
//    }
//
//    private void getAllChannels() {
//        System.out.println("\n[Channel] 모든 채널 조회");
//        channelService.getAllChannels().forEach(System.out::println);
//    }
//
//    private void getChannel() {
//        System.out.println("\n[Channel] 단일 채널 조회 (코드잇, 스프린트)");
//        System.out.println(channelService.getChannel(channels.get(0).getId()));
//        System.out.println(channelService.getChannel(channels.get(1).getId()));
//    }
//
//    private void updateChannel() {
//        System.out.println("\n[Channel] 수정된 채널 정보 (코드잇, 스프린트)");
//        channelService.updateChannel(channels.get(0).getId(), "Codeit");
//        channelService.updateChannel(channels.get(1).getId(), "Sprint");
//        System.out.println(channelService.getChannel(channels.get(0).getId()));
//        System.out.println(channelService.getChannel(channels.get(1).getId()));
//    }
//
//    private void deleteChannel() {
//        System.out.println("\n[Channel] 삭제 후 전체 채널 조회 (Sprint, 부트캠프)");
//        channelService.deleteChannel(channels.get(1).getId()); // Sprint 삭제
//        channelService.getAllChannels().forEach(System.out::println);
//    }
//
//    private void joinChannel() {
//        System.out.println("\n[Channel] 채널 접속 (코드잇 - 민지, 수연, 태현)");
//        channelService.joinChannel(users.get(0).getId(), channels.get(0).getId());
//        channelService.joinChannel(users.get(1).getId(), channels.get(0).getId());
//        channelService.joinChannel(users.get(2).getId(), channels.get(0).getId());
//
//        System.out.println("\n[Channel] 채널 접속 후 조회 ");
//        channelService.getAllChannels().forEach(System.out::println);
//    }
//
//    private void leaveChannel() {
//        System.out.println("\n[Channel] 채널 접속 해제 후 조회 (코드잇 - 수연)");
//        channelService.leaveChannel(users.get(1).getId(), channels.get(0).getId());
//        channelService.getAllChannels().forEach(System.out::println);
//    }
//
//    private void createMessage() {
//        System.out.println("\n[Message] 유효성 검사 후 메시지 생성");
//        messages = Stream.of(
//                        messageService.createMessage(users.get(0).getId(), channels.get(0).getId(), "안녕하세요!"),
//                        messageService.createMessage(users.get(2).getId(), channels.get(0).getId(), "반갑습니다!"),
//                        messageService.createMessage(users.get(2).getId(), channels.get(0).getId(), "코드잇 채널입니다."),
//                        messageService.createMessage(users.get(3).getId(), channels.get(0).getId(), "사용자 예외 처리 테스트"), // 존재하지 않는 사용자 예외 발생 !
//                        messageService.createMessage(users.get(0).getId(), channels.get(1).getId(), "서버 예외 처리 테스트"), // 존재하지 않는 서버 예외 발생
//                        messageService.createMessage(users.get(1).getId(), channels.get(0).getId(), "채널 접속 예외 처리 테스트") // 접속하지 않는 채널 예외 발생
//                )
//                .filter(Objects::nonNull)
//                .toList();
//    }
//
//    private void getAllMessages() {
//        System.out.println("\n[Message] 모든 메시지 조회");
//        messageService.getAllMessages().forEach(System.out::println);
//    }
//
//    private void getMessage() {
//        System.out.println("\n[Message] 단일 메시지 조회");
//        System.out.println(messageService.getMessage(messages.get(0).getId()));
//        System.out.println(messageService.getMessage(messages.get(1).getId()));
//    }
//
//    private void getChannelMessage() {
//        System.out.println("\n[Message] 채널1의 메시지 조회");
//        messageService.getMessagesByChannel(channels.get(0).getId()).forEach(System.out::println);
//    }
//
//    private void updateMessage() {
//        System.out.println("\n[Message] 메시지 수정 (1번 메시지)");
//        messageService.updateMessage(messages.get(0).getId(), "Hello!");
//        System.out.println(messageService.getMessage(messages.get(0).getId()));
//    }
//
//    private void deleteMessage() {
//        System.out.println("\n[Message] 삭제 후 전체 메시지 조회");
//        messageService.deleteMessage(messages.get(1).getId()); // msg2 삭제
//        messageService.getAllMessages().forEach(System.out::println);
//    }
//
//    public static void main(String[] args) {
//        new JavaApplication().run();
//    }
//}