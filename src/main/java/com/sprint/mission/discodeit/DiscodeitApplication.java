package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginReponse;
import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.Channel.ChannelFindRequest;
import com.sprint.mission.discodeit.dto.Channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.dto.User.UserResponse;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
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

        UserCreateRequest user1CreateRequest = UserCreateRequest.builder()
                .username("username1")
                .email("user1@example.com")
                .password("password1")
                .content(false)
                .profileImage(null)
                .profileContentType(null)
                .build();

        UserCreateRequest user2CreateRequest = UserCreateRequest.builder()
                .username("user2")
                .email("user2@example.com")
                .password("password2")
                .content(false)
                .profileImage(null)
                .profileContentType(null)
                .build();

        User user1 = userService.create(user1CreateRequest);
        User user2 = userService.create(user2CreateRequest);
        System.out.println("생성된 유저 1 ID: " + user1.getId());
        System.out.println("생성된 유저 2 ID: " + user2.getId());

        System.out.println("\n====== 유저 로그인 테스트 ======");
        AuthLoginRequest loginRequest = AuthLoginRequest.builder()
                .id(user1.getId())
                .userName(user1CreateRequest.username())
                .password(user1CreateRequest.password())
                .build();

        AuthLoginReponse authLoginReponse = authService.login(loginRequest);
        System.out.println(authLoginReponse);
        System.out.println("\n====== 유저 단일 조회 테스트 ======");

        UserResponse foundUser = userService.find(UserFindRequest.builder()
                .userId(user1.getId())
                .build());
        System.out.println(foundUser);

        System.out.println("\n====== 유저 전체 조회 테스트 ======");

        List<UserResponse> allUsers = userService.findAll();
        for (UserResponse user : allUsers) {
            System.out.println(user);
        }

        System.out.println("\n====== 유저 수정 테스트 ======");

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .userId(user2.getId())
                .newUsername("new_user2")
                .newEmail("new_user2@example.com")
                .newPassword("new_password2")
                .hasProfileImage(false)
                .newProfileImage(null)
                .newProfileContentType(null)
                .build();

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

        PublicChannelCreateRequest publicChannelCreateRequest = PublicChannelCreateRequest.builder()
                .channelName("공지사항")
                .description("모두가 볼 수 있는 공지")
                .build();

        Channel publicChannel = channelService.create(publicChannelCreateRequest);

        PrivateChannelCreateRequest privateChannelCreateRequest = PrivateChannelCreateRequest.builder()
                .participantsIds(List.of(user1.getId()))
                .build();

        // 3. PRIVATE 채널 생성 (user1만 참여)
        Channel privateChannel = channelService.create(privateChannelCreateRequest);

        System.out.println("\n=== PUBLIC 채널 조회 테스트 ===");
        ChannelResponse publicResponse = channelService.find(
                ChannelFindRequest.builder()
                        .id(publicChannel.getId())
                        .build()
        );
        System.out.println(publicResponse);

        // 5. PRIVATE 채널 조회 테스트
        System.out.println("\n=== PRIVATE 채널 조회 테스트 ===");
        ChannelResponse privateResponse = channelService.find(
                ChannelFindRequest.builder()
                        .id(privateChannel.getId())
                        .build()
        );
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
        ChannelUpdateRequest channelUpdateRequest = ChannelUpdateRequest.builder()
                .id(publicChannel.getId())
                .type(ChannelType.PUBLIC)
                .name("새로운 공지")
                .description("업데이트햇음")
                .build();
        channelService.update(channelUpdateRequest);
        publicResponse = channelService.find(ChannelFindRequest.builder()
                .id(publicChannel.getId())
                .build());
        System.out.println(publicResponse);

        System.out.println("\n=== public 채널 삭제 테스트 ===");
        channelService.delete(publicChannel.getId());

        System.out.println("\n=== user2가 볼 수 있는 채널 ===");
        user2Channels = channelService.findAllByUserId(user2.getId());
        user2Channels.forEach(System.out::println);

        // 6. 메시지 테스트 시작
        System.out.println("\n=== 메시지 생성 테스트 ===");

        MessageCreateRequest messageCreateRequest = MessageCreateRequest.builder()
                .content("첫 번째 테스트 메시지입니다.")
                .channelId(privateChannel.getId())
                .authorId(user2.getId())
                .build();

        Message createdMessage = messageService.create(messageCreateRequest, null);
        System.out.println("메시지 생성됨: ID = " + createdMessage.getId());

        System.out.println("\n=== 메시지 조회 테스트 ===");
        Message foundMessage = messageService.find(createdMessage.getId());

        System.out.println("조회된 메시지 내용: " + foundMessage.getContent());
        System.out.println("첨부파일 ID들: " + foundMessage.getAttachmentIds());
    }
}
