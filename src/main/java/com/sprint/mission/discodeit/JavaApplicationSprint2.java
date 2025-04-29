package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplicationSprint2 {
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

        // 레포지토리 초기화
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

//        // JCF 서비스 초기화
//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService(userService);
//        MessageService messageService = new JCFMessageService(userService, channelService);

//        // File 서비스 초기화
//        UserService userService = new FileUserService();
//        ChannelService channelService = new FileChannelService(userService);
//        MessageService messageService = new FileMessageService(userService, channelService);

        // Basic 서비스 초기화
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userService);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);


        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);

        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");
    }
}
