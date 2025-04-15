package com.sprint.mission;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;



public class testAp {
    public static void main(String[] args) {



        ChannelService fileChannelService = new FileChannelService();
        UserService fileUserService = new FileUserService(fileChannelService);
        MessageService fileMessageService = new FileMessageService(fileChannelService);

        Channel channel1 = fileChannelService.createChannel("1번방");
        Channel channel2 = fileChannelService.createChannel("2번방");
        Channel channel3 = fileChannelService.createChannel("3번방");

        System.out.println(channel1.getChannelName());
        System.out.println(channel2.getId());
        System.out.println(channel3.getChannelName());

        User user1 = fileUserService.createUser("김현기",channel1.getId());

        Message message1 = fileMessageService.createMessage("안녕하세요~",channel1.getId(),user1.getId());

        System.out.println(fileMessageService.readMessage(message1.getId()));


    }

}
