package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
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
        JCFUserService userService = new JCFUserService();

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
                System.out.println(user.toString())
        );
        // User 조회 - by name
        userService.read("John").stream().forEach(user ->
                System.out.println(user.toString())
        );
        // User 조회 - by id
        System.out.println(userService.read(user4.getId()).toString());

        // User 수정
        User updatedUser = userService.update(user3.getId(), 3);
        System.out.println("updatedUser = " + updatedUser.toString());

        // User 삭제
        userService.delete(user2.getId());

        // User 삭제 후 결과 조회
        userService.readAll().stream().forEach(user ->
                System.out.println(user.toString())
        );


        /* message service */
        JCFMessageService messageService = new JCFMessageService();

        // 각 User 마다 2개의 Message 생성 및 등록
        Message msg1 = new Message("hello I'am " + user1.getName() + ", this is my first message!", user1);
        Message msg2 = new Message("hello I'am " + user1.getName() + ", this is my second message!", user1);

        Message msg3 = new Message("hello I'am " + user2.getName() + ", this is my first message!", user2);
        Message msg4 = new Message("hello I'am " + user2.getName() + ", this is my second message!", user2);

        Message msg5 = new Message("hello I'am " + user3.getName() + ", this is my first message!", user3);
        Message msg6 = new Message("hello I'am " + user3.getName() + ", this is my second message!", user3);

        Message msg7 = new Message("hello I'am " + user4.getName() + ", this is my first message!", user4);
        Message msg8 = new Message("hello I'am " + user4.getName() + ", this is my second message!", user4);

        Message msg9 = new Message("hello I'am " + user5.getName() + ", this is my first message!", user5);
        Message msg10 = new Message("hello I'am " + user5.getName() + ", this is my second message!", user5);

        messageService.create(msg1);
        messageService.create(msg2);
        messageService.create(msg3);
        messageService.create(msg4);
        messageService.create(msg5);
        messageService.create(msg6);
        messageService.create(msg7);
        messageService.create(msg8);
        messageService.create(msg9);
        messageService.create(msg10);

        System.out.println("================Message Log================");

        // Message 조회 - 전체
        messageService.readAll().stream().forEach(msg ->
                System.out.println(msg.toString())
        );
        // Message 조회 - by id
        System.out.println(messageService.read(msg1.getId()).toString());

        // Message 수정
        Message updatedMsg = messageService.update(msg1.getId(), "updated msg");
        System.out.println("updatedMsg = " + updatedMsg.toString());

        // Message 삭제
        messageService.delete(msg2.getId());

        // Message 삭제 후 결과 조회
        messageService.readAll().stream().forEach(msg ->
                System.out.println(msg.toString())
        );

        /* Channel service */
        JCFChannelService channelService = new JCFChannelService();


        // channel 총 3개 생성 및 등록
        Channel channel1 = new Channel("chat1", user1);
        Channel channel2 = new Channel("chat2", user3);
        Channel channel3 = new Channel("chat3", user5);

        channelService.create(channel1);
        channelService.create(channel2);
        channelService.create(channel3);

        System.out.println("================Channel Log================");
        // Channel 조회 - 전체
        channelService.readAll().stream().forEach(ch ->
                System.out.println(ch.toString())
        );
        // Channel 조회 - by id
        System.out.println(channelService.read(channel1.getId()).toString());

        // Channel 수정
        Channel updatedchannel = channelService.update(channel1.getId(), "updated chat1");
        System.out.println("updatedchannel = " + updatedchannel.toString());

        // Channel 삭제
        channelService.delete(channel3.getId());

        // Channel 삭제 후 결과 조회
        channelService.readAll().stream().forEach(ch ->
                System.out.println(ch.toString())
        );

        // Channel 입장
        channelService.joinChannel(channel1, user2);
        channelService.joinChannel(channel2, user4);
        channelService.joinChannel(channel2, user5);

        // Channel 퇴장
        channelService.leaveChannel(channel2, user3);

        // 참가자 리스트
        channelService.readAttendees(channel2).stream().forEach(user ->
                System.out.println(user.toString())
        );

        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");

    }
}
