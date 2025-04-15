package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileUserService;

public class JavaApplicationSprint2 {
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
        UserService userService = new FileUserService();

        // User 5명 생성 및 등록
        User user1 = new User("John", 20);
        User user2 = new User("John", 60);
        User user3 = new User("Bob", 25);
        User user4 = new User("Alice", 22);
        User user5 = new User("Charlie", 28);
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        userService.create(user4);
        userService.create(user5);

        System.out.println("================User Log================");
        // User 조회 - 전체
        userService.readAll().stream().forEach(user ->
                System.out.println("Get all users : " + user.toString())
        );
//        // User 조회 - by name
//        userService.read("John").stream().forEach(user ->
//                System.out.println("Get by name : " + user.toString())
//        );
        // User 조회 - by id
        System.out.println("Get by id : " + userService.read(user4.getId()).toString());

        // User 수정
        User updatedUser = userService.update(user3.getId(), 3);
        System.out.println("Updated user : " + updatedUser.toString());

        // User 삭제
        userService.delete(user2.getId());

        // User 삭제 후 결과 조회
        userService.readAll().stream().forEach(user ->
                System.out.println("Get all users after deleting 'John(60)' : " + user.toString())
        );

        System.out.println("================Channel Log================");
        /* Channel service */
        ChannelService channelService = new FileChannelService();

        // channel 총 3개 생성 및 등록
        Channel channel1 = new Channel("chat1", user1);
        Channel channel2 = new Channel("chat2", user3);
        Channel channel3 = new Channel("chat3", user5);

        channelService.create(channel1);
        channelService.create(channel2);
        channelService.create(channel3);

        // Channel 조회 - 전체
        channelService.readAll().stream().forEach(ch ->
                System.out.println("Get all channels : " + ch.toString())
        );
        // Channel 조회 - by id
        System.out.println("Get by id : " + channelService.read(channel1.getId()).toString());

        // Channel 수정
        Channel updatedchannel = channelService.update(channel1.getId(), "updated chat1");
        System.out.println("Updated channel : " + updatedchannel.toString());

        // Channel 삭제
        channelService.delete(channel3.getId());

        // Channel 삭제 후 결과 조회
        channelService.readAll().stream().forEach(ch ->
                System.out.println("Get all channels after deleting 'chat3' : " + ch.toString())
        );

        // Channel 입장
        channelService.joinChannel(channel1, user2);
        channelService.joinChannel(channel2, user4);
        channelService.joinChannel(channel2, user5);

        // Channel 퇴장
        channelService.leaveChannel(channel2, user3);

        // 참가자 리스트
        channelService.readAttendees(channel2).stream().forEach(user ->
                System.out.println("Get all attendees on channel 2 : " + user.toString())
        );

        System.out.println("================Message Log================");

//        /* message service */
//        JCFMessageService messageService = new JCFMessageService(userService, channelService);
//
//        // Question : 채널을 만들때 user를 넣어줬는데, message에도 channel을 주입해줘야하나?
//        // 각 User 마다 2개의 Message 생성 및 등록
//        messageService.create(user1, channel1, "hello I'am " + user1.getName() + ", this is my first message!");
//        messageService.create(user1, channel1, "hello I'am " + user1.getName() + ", this is my second message!");
//
//        messageService.create(user2, channel1, "hello I'am " + user2.getName() + ", this is my first message!");
//        messageService.create(user2, channel1, "hello I'am " + user2.getName() + ", this is my second message!");
//
//        messageService.create(user3, channel1, "hello I'am " + user3.getName() + ", this is my first message!");
//        messageService.create(user3, channel1, "hello I'am " + user3.getName() + ", this is my second message!");
//
//        messageService.create(user4, channel2, "hello I'am " + user4.getName() + ", this is my first message!");
//        messageService.create(user4, channel2, "hello I'am " + user4.getName() + ", this is my second message!");
//
//        messageService.create(user5, channel2, "hello I'am " + user5.getName() + ", this is my first message!");
//        messageService.create(user5, channel2, "hello I'am " + user5.getName() + ", this is my second message!");
//
//        // Message 조회 - 전체
//        messageService.readAll().stream().forEach(msg ->
//                System.out.println("Get all messages : " + msg.toString())
//        );
//        // Message 조회 - by id    sample id : 1f431bc6-b20a-3aae-af05-d138303d1154
//        Message msg1 = messageService.read(UUID.fromString("1f431bc6-b20a-3aae-af05-d138303d1154"));
//        System.out.println("Get by id : " + msg1);
//
//        // Message 수정
//        Message updatedMsg = messageService.update(msg1.getId(), "updated msg");
//        System.out.println("Updated message : " + updatedMsg.toString());
//
//        // Message 삭제
//        messageService.delete(msg1.getId());
//
//        // Message 삭제 후 결과 조회
//        messageService.readAll().stream().forEach(msg ->
//                System.out.println("Get all channels after deleting 'msg1' : " + msg.toString())
//        );

        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");

    }
}
