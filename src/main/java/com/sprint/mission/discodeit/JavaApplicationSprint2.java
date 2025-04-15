package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
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
        User johnYoung = new User("John", 20);
        User johnOld = new User("John", 60);
        User bob = new User("Bob", 25);
        User alice = new User("Alice", 22);
        User charlie = new User("Charlie", 28);

        userService.create(johnYoung);
        userService.create(johnOld);
        userService.create(bob);
        userService.create(alice);
        userService.create(charlie);

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
        System.out.println("Get by id : " + userService.read(alice.getId()).toString());

        // User 수정
        User updatedUser = userService.update(bob.getId(), 3);
        System.out.println("Updated user : " + updatedUser.toString());

        // User 삭제
        userService.delete(johnOld.getId());

        // User 삭제 후 결과 조회
        userService.readAll().stream().forEach(user ->
                System.out.println("Get all users after deleting 'John(60)' : " + user.toString())
        );

        System.out.println("================Channel Log================");
        /* Channel service */
        ChannelService channelService = new FileChannelService();

        // channel 총 3개 생성 및 등록
        Channel channel1 = new Channel("chat1", johnYoung);
        Channel channel2 = new Channel("chat2", bob);
        Channel channel3 = new Channel("chat3", charlie);

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
        channelService.joinChannel(channel1, alice);
        channelService.joinChannel(channel2, charlie);

        // Channel 퇴장
        channelService.leaveChannel(channel2, bob);

        // 참가자 리스트
        channelService.readAttendees(channel2).stream().forEach(user ->
                System.out.println("Get all attendees on channel 2 : " + user.toString())
        );

        System.out.println("================Message Log================");

        /* message service */
        MessageService messageService = new FileMessageService(userService, channelService);

        // 각 User 마다 2개의 Message 생성 및 등록
        Message msg1 = messageService.create(johnYoung, channel1, "hello I'am " + johnYoung.getName() + ", this is my first message!");
        Message msg2 = messageService.create(johnYoung, channel1, "hello I'am " + johnYoung.getName() + ", this is my second message!");


        Message msg3 = messageService.create(alice, channel1, "hello I'am " + alice.getName() + ", this is my first message!");
        Message msg4 = messageService.create(alice, channel1, "hello I'am " + alice.getName() + ", this is my second message!");

        Message msg5 = messageService.create(bob, channel2, "hello I'am " + bob.getName() + ", this is my first message!"); // should be "invalid user"
        Message msg6 = messageService.create(bob, channel2, "hello I'am " + bob.getName() + ", this is my second message!"); // should be "invalid user"

        Message msg7 = messageService.create(charlie, channel2, "hello I'am " + charlie.getName() + ", this is my first message!");
        Message msg8 = messageService.create(charlie, channel2, "hello I'am " + charlie.getName() + ", this is my second message!");

        // Message 조회 - 전체
        messageService.readAll().stream().forEach(msg ->
                System.out.println("Get all messages : " + msg.toString())
        );
        System.out.println("Get by id : " + messageService.read(msg1.getId()));

        // Message 수정
        Message updatedMsg = messageService.update(msg1.getId(), "updated msg");
        System.out.println("Updated message : " + updatedMsg.toString());

        // Message 삭제
        messageService.delete(updatedMsg.getId());
//
        // Message 삭제 후 결과 조회
        messageService.readAll().stream().forEach(msg ->
                System.out.println("Get all messages after deleting 'updated msg' : " + msg.toString())
        );

        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");

    }
}
