package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : TestFileUserService
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
public class TestFileUserService {
    public static FileUserService fileUserService;
    public static FileChannelService fileChannelService;
    public static FileMessageService fileMessageService;


    static {
        fileUserService = new FileUserService();
        fileChannelService = new FileChannelService();
        fileMessageService = new FileMessageService(fileChannelService);

        fileChannelService.setFileChannelService(fileMessageService,fileUserService);

    }



    public static void main(String[] args) {

        // USER TEST
//        UUID kateId = fileUserService.registerUser("Kate");
//        UUID danielId = fileUserService.registerUser("daniel");
//        UUID JohnId = fileUserService.registerUser("john");

//        System.out.println(fileUserService.findUserById(kateId).getUsername() +" : "+ kateId);

//        fileUserService.updateUsername(kateId,"John");
//        System.out.println(fileUserService.findUserById(kateId).getUsername() +" : "+ kateId);
//        System.out.println();

//        fileUserService.updateUsername(UUID.fromString("92f24aa0-e1a1-41ce-af2a-0835351c11ad"), "Daniel");
//        fileUserService.deleteUser(UUID.fromString("ecd01408-3e15-419d-a2f5-afa6deb19f69"));
//        fileUserService.deleteUser(UUID.fromString("66e2deaa-e602-48fb-9805-0f406ab14e90"));
        fileUserService.findAllUsers().forEach(user -> System.out.println(user.getUsername()+" : "+user.getId()));
        UUID kateId = UUID.fromString("eb26d05d-2679-4d7f-9fbf-7ae13e7ec736");
        UUID daneilId = UUID.fromString("f62b467b-8363-4131-a631-0954fb262bb1");
        UUID johnId = UUID.fromString("c4704c92-2532-48e8-8655-125fdbd729e9");

        System.out.println("-----------------------------------------------------------------------------------------------------------------------");
//
//        UUID channelId = fileChannelService.createChannel(kateId);
//        fileChannelService.createChannel(kateId);
//        fileChannelService.createChannel(daneilId);
//        fileChannelService.createChannel(johnId);


//        UUID channelId = UUID.fromString("89ef3724-737b-4640-a502-3e768734f6ee");
//        System.out.println(fileChannelService.findChannelById(channelId).getTitle());
//        System.out.println();
//        fileChannelService.findChannelsByUserId(kateId).forEach(channel -> System.out.println(channel.getTitle()));

//        fileChannelService.findAllChannel().forEach(channel -> System.out.println(channel.getTitle()+" : "+channel.getId()));
//        fileChannelService.updateChannelName(UUID.fromString("52de63a1-30e9-45f2-bc7f-3d1e2b70f11b"), "Daniel's private channel");
//        fileChannelService.deleteChannel(UUID.fromString("4297ff9f-4091-4340-83bf-27c131e100f0"));
//        System.out.println();
//        fileChannelService.findAllChannel().forEach(channel -> System.out.println(channel.getTitle()+" : "+channel.getId()));
//        System.out.println("Kate's channel : add Kate, John");
//        fileChannelService.addUserInChannel(UUID.fromString("89ef3724-737b-4640-a502-3e768734f6ee"),UUID.fromString("eb26d05d-2679-4d7f-9fbf-7ae13e7ec736"));
//        fileChannelService.addUserInChannel(UUID.fromString("89ef3724-737b-4640-a502-3e768734f6ee"),UUID.fromString("c4704c92-2532-48e8-8655-125fdbd729e9"));

        System.out.println(fileChannelService.findChannelById(UUID.fromString("89ef3724-737b-4640-a502-3e768734f6ee")).getUsersIds());
    }

}
