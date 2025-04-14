package com.sprint.mission.discodeit.service.file;

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
        UUID kateId = fileUserService.registerUser("Kate");
        System.out.println(fileUserService.findUserById(kateId).getUsername() +" : "+ kateId);

        fileUserService.updateUsername(kateId,"John");
        System.out.println(fileUserService.findUserById(kateId).getUsername() +" : "+ kateId);

//        fileUserService.deleteUser(UUID.fromString("ecd01408-3e15-419d-a2f5-afa6deb19f69"));

        fileUserService.deleteUser(kateId);

        fileUserService.findAllUsers().forEach(user -> System.out.println(user.getUsername()+" : "+user.getId()));

        System.out.println("-----------------------------------------------------------------------------------------------------------------------");



    }
}
