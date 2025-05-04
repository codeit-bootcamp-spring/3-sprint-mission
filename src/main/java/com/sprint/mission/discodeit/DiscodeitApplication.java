package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    static User setupUser(UserService userService) {
        // 기본 생성
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "Alice",
                "pwd1234",
                "Alice@example.com",
                "010-1234-5678",
                "CodeItBootCamp"
                );

        // 선택적 프로필 이미지 적용 ( 없음 처리 )
        // Optional.empty() < 해당 값이 null일 수도 아닐 수도 있는 자리이다
        User user = userService.create(userCreateRequest, Optional.empty());
        System.out.println("신규 유저 등록 : " + user.getUserName());
        return user;
    }

    static Channel setupChannel(ChannelService channelService, UUID userId) {
        List<Channel> channels = new ArrayList<>();

        // PUBLIC CHANNEL
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest(
                "Test",
                ChannelType.PUBLIC,
                "공지 채널"
        );
        Channel publicChannel = channelService.create(publicChannelCreateRequest);
        System.out.println("PUBLIC CHANNEL CREATED : " + publicChannel.getChannelName());
        channels.add(publicChannel);

        // PRIVATE CHANNEL
        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(
                ChannelType.PRIVATE,
                // 단일 유저만 참여함
                List.of(userId)
        );
        Channel privateChanel = channelService.create(privateChannelCreateRequest);
        System.out.println("PRIVATE CHANNEL CREATED");
        channels.add(privateChanel);
        return privateChanel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                "메세지 생성 테스트를 위한 내용입니다",
                channel.getChannelId(),
                author.getUserId()
        );

        // 선택적 첨부파일 처리
        List<BinaryContentCreateRequest> attachments = new ArrayList<>();

        // 메세지 생성
        Message message = messageService.create(messageCreateRequest, attachments);
        System.out.println("메세지 생성 완료 ( " + message.getMessageId() + " )");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService, user.getUserId());

        // 테스트
        messageCreateTest(messageService, channel, user);


    }

}
