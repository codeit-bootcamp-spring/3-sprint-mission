package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateResponse;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    private static User user;
    private static Channel channel;
    private static Message message;

    static User setupUser(UserService userService) {
//        user = new User("냥냥이", "woody@codeit.com", "woody1234");

        // 프로필사진 없는 유저 생성
        UserCreateResponse userCreateResponse = userService.create(new UserCreateRequest("냥냥이", "woody@codeit.com", "woody1234", null));
        System.out.println("----------프로필없는 유저 생성----------");
        System.out.println(userCreateResponse.toString());

        // 프로필사진 있는  유저 생성
        UserCreateResponse userCreateResponseWithProfile = userService.create(new UserCreateRequest("댕댕이", "woody@codeit.com", "woody1234", UUID.randomUUID()));
        System.out.println("----------프로필있는 유저 생성----------");
        System.out.println(userCreateResponseWithProfile.toString());


        // 테스트
        System.out.println("---------- 유저 findById ----------");
        userService.find(userCreateResponse.user().getId());
        System.out.println(userCreateResponse.toString());
        System.out.println("---------- 유저 findAll ----------");
        List<UserResponse> users = userService.findAll();
        for (UserResponse userRes : users) {
            System.out.println("name : " + userRes.name());
        }
        System.out.println("---------- 유저 update ----------");
        UserCreateResponse updatedUserResponse = userService.update(new UserUpdateRequest(userCreateResponseWithProfile.user().getId(), "댕댕이 진화함", null, null, null));
        System.out.println(updatedUserResponse.user().getName() + " 로 업데이트 됨");

        System.out.println("---------- 유저 delete 후 결과 조회 ----------");
        userService.delete(userCreateResponseWithProfile.user().getId());
        for (UserResponse userRes : userService.findAll()) {
            System.out.println("name : " + userRes.name());
        }

        return userCreateResponse.user();
    }

    static Channel setupChannel(ChannelService channelService) {
        channel = new Channel("공지", ChannelType.PUBLIC, "공지 채널입니다.", user.getId());

        return channelService.create(channel);
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        message = new Message("안녕하세요. 저는 " + author.getName() + " 입니다.", author.getId(), channel.getId(), Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        messageService.create(message);
        System.out.println("메시지 생성: " + message.toString());
    }

    public static void main(String[] args) {

        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service Start🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️");

        ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        /* 빈 목록 */
//        for (String beanName : context.getBeanDefinitionNames()) {
//            System.out.println("beanName :" + beanName);
//        }

        /*테스트를 위해 bean을 직접 불러옴 */
        UserService userService = context.getBean(BasicUserService.class);
        ChannelService channelService = context.getBean(BasicChannelService.class);
        MessageService messageService = context.getBean(BasicMessageService.class);

        // 셋업
        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);


//        messageCreateTest(messageService, channel, user);


        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");
    }
}
