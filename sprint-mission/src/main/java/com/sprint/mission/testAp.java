package com.sprint.mission;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
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

        System.out.println("----채널 생성----");
        Channel channel1 = fileChannelService.createChannel("1번방");
        Channel channel2 = fileChannelService.createChannel("2번방");
        Channel channel3 = fileChannelService.createChannel("3번방");
        System.out.println("-- 채널 이름 및 ID 확인 --");
        System.out.println(channel1.getChannelName());
        System.out.println(channel2.getId());
        System.out.println(channel3.getChannelName());

        System.out.println("---- 유저 생성 ----");
        User user1 = fileUserService.createUser("김현기",channel1.getId());
        System.out.println("---- 메시지 생성 ----");
        Message message1 = fileMessageService.createMessage("안녕하세요~",channel1.getId(),user1.getId());

        FileChannelRepository fileChannelRepository = new FileChannelRepository();
        fileChannelRepository.save(channel1);




    }

}
