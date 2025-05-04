package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.dto.AuthLogin.AuthLoginReponse;
import com.sprint.mission.discodeit.service.dto.AuthLogin.AuthLoginRequest;
import com.sprint.mission.discodeit.service.dto.Channel.ChannelFindRequest;
import com.sprint.mission.discodeit.service.dto.Channel.ChannelResponse;
import com.sprint.mission.discodeit.service.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.service.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.service.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.service.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.service.dto.User.UserResponse;
import com.sprint.mission.discodeit.service.dto.User.UserUpdateRequest;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthService authService = context.getBean(AuthService.class);

        System.out.println("====== 유저 생성 테스트 ======");

        UserCreateRequest request1 = new UserCreateRequest(
                "user1",
                "user1@example.com",
                "password1",
                false,
                null,
                null
        );

        UserCreateRequest request2 = new UserCreateRequest(
                "user2",
                "user2@example.com",
                "password2",
                false,
                null,
                null
        );

        User user1 = userService.create(request1);
        User user2 = userService.create(request2);
        System.out.println("생성된 유저 1 ID: " + user1.getId());
        System.out.println("생성된 유저 2 ID: " + user2.getId());

        System.out.println("\n====== 유저 로그인 테스트 ======");
        AuthLoginRequest request3 = new AuthLoginRequest(
                user1.getId(),
                user1.getUsername(),
                user1.getPassword()
        );
        AuthLoginReponse authLoginReponse = authService.login(request3);
        System.out.println(authLoginReponse);
        System.out.println("\n====== 유저 단일 조회 테스트 ======");

        UserFindRequest findRequest = new UserFindRequest(user1.getId());
        UserResponse foundUser = userService.find(findRequest);
        System.out.println(foundUser);

        System.out.println("\n====== 유저 전체 조회 테스트 ======");

        List<UserResponse> allUsers = userService.findAll();
        for (UserResponse user : allUsers) {
            System.out.println(user);
        }

        System.out.println("\n====== 유저 수정 테스트 ======");

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                user2.getId(),
                "new_user2",
                "new_user2@example.com",
                "new_password2",
                false,
                null,
                null
        );

        User updatedUser = userService.update(updateRequest);

        System.out.println("수정된 유저:");
        System.out.println(updatedUser);

        System.out.println("\n====== 유저1 삭제 후 전체 조회 테스트 ======");
        userService.delete(user1.getId());

        List<UserResponse> afterDeleteUsers = userService.findAll();
        for (UserResponse user : afterDeleteUsers) {
            System.out.println(user);
        }

        System.out.println("\n====== 유저 테스트 완료 ======");

        System.out.println("\n====== 채널 테스트 시작 ======");

        Channel publicChannel = channelService.create(new PublicChannelCreateRequest(
                "공지사항", "모두가 볼 수 있는 공지"
        ));

        // 3. PRIVATE 채널 생성 (user1만 참여)
        Channel privateChannel = channelService.create(new PrivateChannelCreateRequest(
                List.of(user1.getId())
        ));

        // 4. PUBLIC 채널 조회 테스트
        System.out.println("\n=== PUBLIC 채널 조회 테스트 ===");
        ChannelResponse publicResponse = channelService.find(new ChannelFindRequest(publicChannel.getId()));
        System.out.println(publicResponse);

        // 5. PRIVATE 채널 조회 테스트
        System.out.println("\n=== PRIVATE 채널 조회 테스트 ===");
        ChannelResponse privateResponse = channelService.find(new ChannelFindRequest(privateChannel.getId()));
        System.out.println(privateResponse);

        // 4. user1이 볼 수 있는 채널 확인
        System.out.println("\n=== user1이 볼 수 있는 채널 ===");
        List<ChannelResponse> user1Channels = channelService.findAllByUserId(user1.getId());
        user1Channels.forEach(System.out::println);

        ReadStatusRepository readStatusRepository = context.getBean(ReadStatusRepository.class);

        System.out.println("\n=== private 채널 참여자 ReadStatus ===");
        readStatusRepository.findAllByChannelId(privateChannel.getId()).forEach(System.out::println);

        // 5. user2가 볼 수 있는 채널 확인
        System.out.println("\n=== user2가 볼 수 있는 채널 ===");
        List<ChannelResponse> user2Channels = channelService.findAllByUserId(user2.getId());
        user2Channels.forEach(System.out::println);

        System.out.println("\n=== public 채널 업데이트 테스트 ===");
        ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(publicChannel.getId(), ChannelType.PUBLIC,
                "새로운 공지", "업데이트햇음");
        channelService.update(channelUpdateRequest);
        publicResponse = channelService.find(new ChannelFindRequest(publicChannel.getId()));
        System.out.println(publicResponse);

        System.out.println("\n=== public 채널 삭재 테스트 ===");
        channelService.delete(publicChannel.getId());

        System.out.println("\n=== user2가 볼 수 있는 채널 ===");
        user2Channels = channelService.findAllByUserId(user2.getId());
        user2Channels.forEach(System.out::println);

        // 6. 메시지 테스트 시작
        System.out.println("\n=== 메시지 생성 테스트 ===");

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                "첫 번째 테스트 메시지입니다.",
                privateChannel.getId(),
                user2.getId()
        );

        Message createdMessage = messageService.create(messageCreateRequest, null);
        System.out.println("메시지 생성됨: ID = " + createdMessage.getId());

        System.out.println("\n=== 메시지 조회 테스트 ===");
        Message foundMessage = messageService.find(createdMessage.getId());

        System.out.println("조회된 메시지 내용: " + foundMessage.getContent());
        System.out.println("첨부파일 ID들: " + foundMessage.getAttachmentIds());

    }

}
