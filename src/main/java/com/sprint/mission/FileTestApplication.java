package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FileTestApplication {
    public static void main(String[] args) {

        // JCFRepository Test
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        UserRepository userRepository = new JCFUserRepository();

        // FileRepository Test
        ChannelRepository channelRepository = new FileChannelRepository();
        UserRepository userRepository = new FileUserRepository();

        ChannelService channelService = new BasicChannelService(channelRepository);
        UserService userService = new BasicUserService(userRepository, channelService);
        MessageService messageService = new BasicMessageService(channelRepository);

        User user = new User("kangho", "1234");
        User user2 = new User("test", "1234");
        userService.createUser(user);
        userService.createUser(user2);

        // 유저 단건 조회
        log("유저 단건 조회", () -> userService.getUser(user2.getId()));

        // 전체 유저 조회
        logAll("전체 유저 조회", userService.getAllUsers(), System.out::println);

        // 유저 비밀번호 수정
        log("비밀번호 수정", () -> userService.modifyPassword(user.getId(), "1234", "0000"));

        // 수정된 데이터 조회
        log("유저 데이터 수정 후", () -> userService.getUser(user.getId()));

        // 채널 생성
        Channel channel = channelService.createChannel("codeit", "코드잇 커뮤니티", user);
        Channel channel2 = channelService.createChannel("FileIO", "코드잇 커뮤니티", user2);

        log("채널 참가", () -> channelService.joinChannel(channel, user2));
        log("채널 참가", () -> channelService.joinChannel(channel2, user));

        logAll("전체 채널 조회", channelService.getAllChannels(), System.out::println);

        // 채널명 수정
        // 채널의 주인이 아닌경우
        log("채널명 수정", () -> channelService.modifyChannelName(channel.getId(), user2, "수정한 채널명"));

        // 채널의 주인인 경우
        log("채널명 수정", () -> channelService.modifyChannelName(channel.getId(), user, "수정한 채널명"));

        // 채널 설명 수정
        // 채널의 주인이 아닌경우
        log("채널 설명 수정", () -> channelService.modifyChannelDescription(channel.getId(), user2, "수정한 채널 설명"));


        // 채널의 주인인 경우
        log("채널 설명 수정", () -> channelService.modifyChannelDescription(channel.getId(), user, "수정한 채널 설명"));
        System.out.println("---------------------------------");

        System.out.println("----채널 수정 이후 채널 목록-----");
        logAll("채널 목록", channelService.getAllChannels(), System.out::println);

        // 채널 강퇴
        // 채널의 주인이 아닐경우
//        log("유저 강퇴", () -> channelService.kickOutChannel(channel.getId(), user, user2));

        // 채널의 주인일 경우
        log("유저 강퇴", () -> channelService.kickOutChannel(channel.getId(), user2, user));

        System.out.println("--------강퇴 이후 채널 유저------");
        channelService.getChannel(channel.getId())
                .ifPresent(ch -> logAll("채널 유저", ch.getMembers(), System.out::println));

        channelService.getChannel(channel2.getId())
                .ifPresent(ch -> logAll("채널 유저", ch.getMembers(), System.out::println));

        // 메세지 보내기
        Message message = new Message(user, "안녕하세요");
        Message message2 = new Message(user2, "hi~");

        log("메세지 전송", () -> channelService.addMessageToChannel(channel.getId(), message));

        logAll("채널의 메세지 확인", messageService.getChannelMessages(channel), e ->
                System.out.println(e.getSender().getUserName() + " : " + e.getContent())
        );

        // 메세지 삭제
        // 본인 외 메세지 삭제일 경우
//        log("메세지 삭제", () -> messageService.deleteMessage(channel, message2, user));

        // 본인이 작성한 메세지일 경우
        log("메세지 삭제", () -> messageService.deleteMessage(channel, message, user));

        logAll("메세지 삭제 후", messageService.getChannelMessages(channel), e ->
                System.out.println(e.getContent())
        );

        // 채널 삭제
        log("채널 삭제", () -> channelService.deleteChannel(channel.getId(), user));

        log("채널 삭제 후", () -> channelService.getChannel(channel.getId()));

        logAll("채널", channelService.getAllChannels(), System.out::println);

    }
    private static <T> void log(String action, Supplier<T> supplier) {
        System.out.println(action + " : " + supplier.get());
    }
    private static <T> void log(String action, BooleanSupplier supplier) {
        System.out.println(action + " : " + (supplier.getAsBoolean() ? (action + " 성공") :(action + " 실패")));
    }
    private static <T> void log(String action, BooleanSupplier supplier, String content) {
        System.out.println(action + " : " + (supplier.getAsBoolean() ? (action + " 성공 : " + content) :(action + " 실패 ")));
    }

    private static <T> void logAll(String action, Collection<T> list, Consumer<T> consumer) {
        System.out.println(action + " : ");
        list.forEach(consumer);
    }
}
