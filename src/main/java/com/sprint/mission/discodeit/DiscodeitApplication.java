package com.sprint.mission.discodeit;

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

@SpringBootApplication
public class DiscodeitApplication {

    private static User user;
    private static Channel channel;
    private static Message message;

    static User setupUser(UserService userService) {
        user = new User("냥냥이", 20, "woody@codeit.com", "woody1234");

        return userService.create(user);
    }

    static Channel setupChannel(ChannelService channelService) {
        channel = new Channel("공지", ChannelType.PUBLIC, "공지 채널입니다.", user.getId());

        return channelService.create(channel);
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        message = new Message("안녕하세요. 저는 " + author.getName() + " 입니다.", author.getId(), channel.getId());
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
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);


        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");
    }
}
