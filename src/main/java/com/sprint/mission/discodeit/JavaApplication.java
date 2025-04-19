package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    private static User user1;
    private static User user2;
    private static User user3;
    private static User user4;
    private static User user5;
    //
    private static Channel channel1;
    private static Channel channel2;
    private static Channel channel3;
    //
    private static Message message1;
    private static Message message2;
    private static Message message3;
    private static Message message4;
    private static Message message5;
    private static Message message6;
    private static Message message7;
    private static Message message8;

    public static void userCRUDTest(UserService userService) {
        System.out.println("================User Log================");

        // User 5명 생성 및 등록
        user1 = new User("John", 20, "john@gmail.com", "1234");
        user2 = new User("John", 60, "johnOld@gmail.com", "1234");
        user3 = new User("Bob", 25, "bob@gmail.com", "1234");
        user4 = new User("Alice", 22, "alice@gmail.com", "1234");
        user5 = new User("Charlie", 28, "charlie@gmail.com", "1234");

        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        userService.create(user4);
        userService.create(user5);

        // User 조회 - 전체
        userService.findAll().stream().forEach(user ->
                System.out.println("Get all users : " + user.toString())
        );
        // User 조회 - by name
        userService.find("John").stream().forEach(user ->
                System.out.println("Get by name : " + user.toString())
        );
        // User 조회 - by id
        System.out.println("Get by id : " + userService.find(user4.getId()).toString());

        // User 수정
        User updatedUser = userService.update(user3.getId(), null, 3, null, null);
        System.out.println("Updated user : " + updatedUser.toString());

        // User 삭제
        userService.delete(user2.getId());

        // User 삭제 후 결과 조회
        userService.findAll().stream().forEach(user ->
                System.out.println("Get all users after deleting 'John(60)' : " + user.toString())
        );

    }

    public static void channelCRUDTest(ChannelService channelService) {
        System.out.println("================Channel Log================");

        // channel 총 3개 생성 및 등록
        channel1 = new Channel("channel 1", ChannelType.PRIVATE, "This is channel 1", user1.getId());
        channel2 = new Channel("channel 2", ChannelType.PUBLIC, "This is channel 2", user4.getId());
        channel3 = new Channel("channel 3", ChannelType.PUBLIC, "This is channel 3", user5.getId());
        channelService.create(channel1);
        channelService.create(channel2);
        channelService.create(channel3);

        // Channel 조회 - 전체
        channelService.findAll().stream().forEach(ch ->
                System.out.println("Get all channels : " + ch.toString())
        );
        // Channel 조회 - by id
        System.out.println("Get by id : " + channelService.find(channel1.getId()).toString());

        // Channel 수정
        Channel updatedchannel = channelService.update(channel1.getId(), "new name channel 1", null);
        System.out.println("Updated channel : " + updatedchannel.toString());

        // Channel 삭제
        channelService.delete(channel3.getId());

        // Channel 삭제 후 결과 조회
        channelService.findAll().stream().forEach(ch ->
                System.out.println("Get all channels after deleting 'chat3' : " + ch.toString())
        );

        // 참가자 리스트
        channelService.findAttendeesByChannel(channel2.getId()).stream().forEach(user ->
                System.out.println("Get all attendees on channel 2 : " + user.toString())
        );

        // Channel 입장
        channelService.addAttendeeToChannel(channel1.getId(), user3.getId());
        // Channel 입장 후 참가자 리스트
        channelService.findAttendeesByChannel(channel1.getId()).stream().forEach(user ->
                System.out.println("Get all attendees on channel 1 after joining : " + user.toString())
        );

        // Channel 퇴장
        channelService.removeAttendeeToChannel(channel2.getId(), user4.getId());
        // Channel 퇴장 후 참가자 리스트
        channelService.findAttendeesByChannel(channel2.getId()).stream().forEach(user ->
                System.out.println("Get all attendees on channel 2 after leaving : " + user.toString())
        );
    }

    public static void messageCRUDTest(MessageService messageService) {
        System.out.println("================Message Log================");

        // 각 User 마다 2개의 Message 생성 및 등록
        message1 = new Message("hello I'am " + user1.getName() + ", this is my first message!", channel1.getId(), user1.getId());
        message2 = new Message("hello I'am " + user1.getName() + ", this is my second message!", channel1.getId(), user1.getId());

        message3 = new Message("hello I'am " + user3.getName() + ", this is my first message!", channel1.getId(), user3.getId());
        message4 = new Message("hello I'am " + user3.getName() + ", this is my second message!", channel1.getId(), user3.getId());

        message5 = new Message("hello I'am " + user4.getName() + ", this is my first message!", channel2.getId(), user4.getId());
        message6 = new Message("hello I'am " + user4.getName() + ", this is my second message!", channel2.getId(), user4.getId());

        message7 = new Message("hello I'am " + user5.getName() + ", this is my first message!", channel2.getId(), user5.getId());
        message8 = new Message("hello I'am " + user5.getName() + ", this is my second message!", channel2.getId(), user5.getId());
        messageService.create(message1);
        messageService.create(message2);
        messageService.create(message3);
        messageService.create(message4);
        messageService.create(message5);
        messageService.create(message6);
        messageService.create(message7);
        messageService.create(message8);
        // Message 조회 - 전체
        messageService.findAll().stream().forEach(msg ->
                System.out.println("Get all messages : " + msg.toString())
        );
        // Message 조회 - by id
        Message searchedMsg = messageService.find(message1.getId());
        System.out.println("Get by id : " + searchedMsg);

        // Message 수정
        Message updatedMsg = messageService.update(searchedMsg.getId(), "updated msg");
        System.out.println("Updated message : " + updatedMsg.toString());

        // 특정 channel의 메세지 가져오기
        messageService.findMessagesByChannel(channel2.getId()).forEach(System.out::println);

        // Message 삭제
        messageService.delete(searchedMsg.getId());

        // Message 삭제 후 결과 조회
        messageService.findAll().stream().forEach(msg ->
                System.out.println("Get all channels after deleting 'msg1' : " + msg.toString())
        );

    }

    public static void main(String[] args) {

        /*
        객체 별
        [ ] 등록
        [ ] 조회(단건, 다건)
        [ ] 수정
        [ ] 수정된 데이터 조회
        [ ] 삭제
        [ ] 조회를 통해 삭제되었는지 확인
        */

        System.out.println("🏃🏃🏃Service Start🏃🏃🏃");

        /* user service */
        UserService userService = new JCFUserService();
        userCRUDTest(userService);

        /* Channel service */
        ChannelService channelService = new JCFChannelService(userService);
        channelCRUDTest(channelService);

        /* message service */
        MessageService messageService = new JCFMessageService(userService, channelService);
        messageCRUDTest(messageService);

        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");

    }


}
