package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {


    public static void main(String[] args) {

        JCFChannelService channelService = new JCFChannelService();
        JCFUserService userService = new JCFUserService();
        JCFMessageService messageService = new JCFMessageService(channelService);

        System.out.println("----- 유저 등록 --------\n");
        User user1 = userService.createUser("KHG");
        User user2 = userService.createUser("PM");


        System.out.println("----- 유저 조회 단건 --------\n");
        System.out.println(userService.readUser(user1.getId()));
        System.out.println(userService.readUser(user2.getId()));

        System.out.println("----- 유저 조회 다건 --------\n");
        System.out.println(userService.readUsers());


        System.out.println("----- 유저 이름 수정 --------\n");
        userService.updateUser(user1.getId(), "김현기");
        userService.updateUser(user2.getId(), "박민");
        System.out.println(userService.readUser(user1.getId()));
        System.out.println(userService.readUser(user2.getId()));

        System.out.println("----- 유저 삭제 --------\n");
        userService.deleteUser(user1.getId());
        userService.deleteUser(user2.getId());
        System.out.println(userService.readUser(user1.getId()));
        System.out.println(userService.readUser(user2.getId()));


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

        System.out.println("----- 채널 삭제 --------\n");
        channelService.deleteChannel(channel1.getId());
        channelService.deleteChannel(channel2.getId());
        channelService.deleteChannel(channel3.getId());
        System.out.println(channelService.readChannel(channel1.getId()));
        System.out.println(channelService.readChannel(channel2.getId()));
        System.out.println(channelService.readChannel(channel3.getId()));

        System.out.println("-----------------------------------------------------------------------------------\n");

        User user3 = userService.createUser("양주원");
        User user4 = userService.createUser("조원준");

        Channel channel4 = channelService.createChannel("공격대");



        System.out.println("----- 메시지 등록 --------\n");
        Message message1 = messageService.createMessage("안녕하세요!",channel4.getId(),user3.getId());
        Message message2 = messageService.createMessage("반갑습니다.",channel4.getId(),user4.getId());

        System.out.println("----- 메시지 조회 단건 --------\n");
        System.out.println(messageService.readMessage(message1.getId()));
        System.out.println(messageService.readMessage(message2.getId()));

        System.out.println("----- 메시지 조회 다건 --------\n");
        System.out.println(messageService.readMessages());


        System.out.println("----- 메시지 이름 수정 --------\n");
        messageService.updateMessage(message1.getId(),"배가 고프네요 ㅎㅎ;");
        messageService.updateMessage(message2.getId(),"저는 잠이 옵니다 ㅠㅠ");
        System.out.println(messageService.readMessage(message1.getId()));
        System.out.println(messageService.readMessage(message2.getId()));
    //
    //
        System.out.println("----- 메시지 삭제 --------\n");
        messageService.deleteMessage(message1.getId());
        messageService.deleteMessage(message2.getId());
        System.out.println(messageService.readMessage(message1.getId()));






    }
}
