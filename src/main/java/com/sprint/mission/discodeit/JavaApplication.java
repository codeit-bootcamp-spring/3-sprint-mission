package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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
        User user1 = userService.create("John", 20, "john@gmail.com", "1234");
        User user2 = userService.create("John", 60, "johnOld@gmail.com", "1234");
        User user3 = userService.create("Bob", 25, "bob@gmail.com", "1234");
        User user4 = userService.create("Alice", 22, "alice@gmail.com", "1234");
        User user5 = userService.create("Charlie", 28, "charlie@gmail.com", "1234");

        System.out.println("================User Log================");
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

        System.out.println("================Channel Log================");
        /* Channel service */
        JCFChannelService channelService = new JCFChannelService();

        // channel 총 3개 생성 및 등록
        Channel channel1 = channelService.create("channel 1", ChannelType.PRIVATE, "This is channel 1");
        Channel channel2 = channelService.create("channel 2", ChannelType.PUBLIC, "This is channel 2");
        Channel channel3 = channelService.create("channel 3", ChannelType.PUBLIC, "This is channel 3");


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

//        // Channel 입장
//        channelService.joinChannel(channel1, user2);
//        channelService.joinChannel(channel2, user4);
//        channelService.joinChannel(channel2, user5);
//
//        // Channel 퇴장
//        channelService.leaveChannel(channel2, user3);
//
//        // 참가자 리스트
//        channelService.readAttendees(channel2).stream().forEach(user ->
//                System.out.println("Get all attendees on channel 2 : " + user.toString())
//        );

        System.out.println("================Message Log================");

        /* message service */
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        // 각 User 마다 2개의 Message 생성 및 등록
        Message message1 = messageService.create("hello I'am " + user1.getName() + ", this is my first message!", channel1.getId(), user1.getId());
        Message message2 = messageService.create("hello I'am " + user1.getName() + ", this is my second message!", channel1.getId(), user1.getId());

        Message message3 = messageService.create("hello I'am " + user3.getName() + ", this is my first message!", channel1.getId(), user3.getId());
        Message message4 = messageService.create("hello I'am " + user3.getName() + ", this is my second message!", channel1.getId(), user3.getId());

        Message message5 = messageService.create("hello I'am " + user4.getName() + ", this is my first message!", channel2.getId(), user4.getId());
        Message message6 = messageService.create("hello I'am " + user4.getName() + ", this is my second message!", channel2.getId(), user4.getId());

        Message message7 = messageService.create("hello I'am " + user5.getName() + ", this is my first message!", channel2.getId(), user5.getId());
        Message message8 = messageService.create("hello I'am " + user5.getName() + ", this is my second message!", channel2.getId(), user5.getId());

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

        // Message 삭제
        messageService.delete(searchedMsg.getId());

        // Message 삭제 후 결과 조회
        messageService.findAll().stream().forEach(msg ->
                System.out.println("Get all channels after deleting 'msg1' : " + msg.toString())
        );

        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");

    }
}
