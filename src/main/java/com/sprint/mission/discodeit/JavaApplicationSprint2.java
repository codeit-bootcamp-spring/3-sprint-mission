package com.sprint.mission.discodeit;

public class JavaApplicationSprint2 {
//    private static User user;
//    private static Channel channel;
//    private static Message message;
//
//    static User setupUser(UserService userService) {
//        user = new User("냥냥이", 20, "woody@codeit.com", "woody1234");
//
//        return userService.create(user);
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        channel = new Channel("공지", ChannelType.PUBLIC, "공지 채널입니다.", user.getId());
//
//        return channelService.create(channel);
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        message = new Message("안녕하세요. 저는 " + author.getName() + " 입니다.", author.getId(), channel.getId());
//        messageService.create(message);
//        System.out.println("메시지 생성: " + message.toString());
//    }
//
//    public static void main(String[] args) {
//        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service Start🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️");
//
//        // 레포지토리 초기화
//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();
//
////        // JCF 서비스 초기화
////        UserService userService = new JCFUserService();
////        ChannelService channelService = new JCFChannelService(userService);
////        MessageService messageService = new JCFMessageService(userService, channelService);
//
////        // File 서비스 초기화
////        UserService userService = new FileUserService();
////        ChannelService channelService = new FileChannelService(userService);
////        MessageService messageService = new FileMessageService(userService, channelService);
//
//        // Basic 서비스 초기화
//        UserService userService = new BasicUserService(userRepository);
//        ChannelService channelService = new BasicChannelService(channelRepository, userService);
//        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);
//
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//
//        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");
//    }
}
