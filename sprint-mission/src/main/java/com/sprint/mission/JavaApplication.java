package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {


    public static void main(String[] args) {
        // 구현체의 생성자를 호출해 인스턴스 생성
        JCFChannelService channelService = new JCFChannelService();
        JCFUserService userService = new JCFUserService(channelService);
        JCFMessageService messageService = new JCFMessageService(channelService);

        System.out.println("-----------------------------------------------------------------------------------\n");

        System.out.println("----- 채널 등록 --------\n");

        Channel channel1 = channelService.createChannel("토끼방");
        Channel channel2 = channelService.createChannel("호랑이방");
        Channel channel3 = channelService.createChannel("카멜레온방");

        System.out.println("----- 채널 조회 단건 --------\n");

        System.out.println(channelService.readChannel(channel1.getId()));
        System.out.println(channelService.readChannel(channel2.getId()));
        System.out.println(channelService.readChannel(channel3.getId()));

        System.out.println("----- 채널 조회 다건 --------\n");

        System.out.println(channelService.readChannels());

        System.out.println("----- 채널 이름 수정 --------\n");

        channelService.updateChannel(channel1.getId(), "검은 토끼방");
        channelService.updateChannel(channel2.getId(),"하얀 호랑이방");
        System.out.println(channelService.readChannel(channel1.getId()));
        System.out.println(channelService.readChannel(channel2.getId()));
        System.out.println(channelService.readChannel(channel3.getId()));

        System.out.println("----- 채널 삭제 2,3 --------\n");
        channelService.deleteChannel(channel2.getId());
        channelService.deleteChannel(channel3.getId());
        System.out.println(channelService.readChannel(channel1.getId()));
        System.out.println(channelService.readChannel(channel2.getId()));
        System.out.println(channelService.readChannel(channel3.getId()));

        System.out.println("-----------------------------------------------------------------------------------\n");

        System.out.println("----- 유저 등록 --------\n");

        User user1 = userService.createUser("KHG",channel1.getId()); // 유저 1 등록
        User user2 = userService.createUser("PM",channel1.getId()); // 유저 2 등록


        System.out.println("----- 유저 조회 단건 --------\n");

        System.out.println(userService.readUser(user1.getId())); // 유저 1 조회
        System.out.println(userService.readUser(user2.getId())); // 유저 2 조회

        System.out.println("----- 유저 조회 다건 --------\n");

        System.out.println(userService.readUsers()); // 모든 유저 조회
//
//
        System.out.println("----- 유저 이름 수정 --------\n");

        userService.updateUser(user1.getId(), "김현기");
        userService.updateUser(user2.getId(), "박민");
        // 유저명 수정 후 조회
        System.out.println(userService.readUser(user1.getId()));
        System.out.println(userService.readUser(user2.getId()));

        System.out.println("----- 유저1 삭제 --------\n");
        // 유저삭제 후에 삭제가 잘 되었는지 확인
        userService.deleteUser(user1.getId());
        System.out.println(userService.readUser(user1.getId()));
        System.out.println(userService.readUser(user2.getId()));
//
//
        System.out.println("-----------------------------------------------------------------------------------\n");

        // 메시지를 받을 새로운 채널 생성
        Channel channel4 = channelService.createChannel("코딩룸");

        // 메시지를 보낼 새로운 유저 생성
        User user3 = userService.createUser("양주원",channel4.getId());
        User user4 = userService.createUser("조원준",channel4.getId());

//
//
//
//
        System.out.println("----- 메시지 등록 --------\n");

        Message message1 = messageService.createMessage("안녕하세요!",channel4.getId(),user3.getId());
        Message message2 = messageService.createMessage("반갑습니다.",channel4.getId(),user4.getId());

        System.out.println("----- 메시지 조회 단건 --------\n");
//
        System.out.println(messageService.readMessage(message1.getId()));
        System.out.println(messageService.readMessage(message2.getId()));

        System.out.println("----- 메시지 조회 다건 --------\n");

        System.out.println(messageService.readMessages());


        System.out.println("----- 메시지 이름 수정 --------\n");

        messageService.updateMessage(message1.getId(),"배가 고프네요 ㅎㅎ;");
        messageService.updateMessage(message2.getId(),"저는 잠이 옵니다 ㅠㅠ");
        System.out.println(messageService.readMessage(message1.getId()));
        System.out.println(messageService.readMessage(message2.getId()));


        System.out.println("----- 메시지 2번 삭제 --------\n");

        messageService.deleteMessage(message2.getId());
        System.out.println(messageService.readMessage(message1.getId()));
        System.out.println(messageService.readMessage(message2.getId()));


        System.out.println("-----------------------------------------------------------------------------------\n");





    }
}
