package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.List;

public class JavaApplication2 {
    private static FileUserService fileUserService = new FileUserService();
    private static FileChannelService fileChannelService = new FileChannelService();
    private static FileMessageService fileMessageService = new FileMessageService();

    private static List<User> users;
    private static List<Channel> channels;
    private static List<Message> messages;

    private static void initServices() {
        fileUserService = new FileUserService();
        fileChannelService = new FileChannelService();
        fileMessageService = new FileMessageService();
    }

    private static void testUserServices() {
        List<String> names = List.of("Jane", "Bob", "John");
        users = names.stream()
                .map(fileUserService::createUser)
                .toList();


    }

    private static void createUser() {

    }

    private static void testChannelServices() {}

    private static void testMessageServices() {}

    public static void main(String[] args) {


        fileUserService.getAllUsers();

        // 2. User 단일 조회
        fileUserService.getUserByName("Jane");
        fileUserService.getUserByName("Tom");

        // 3. User 수정
//        fileUserService.updateUser();
//        fileUserService.updateUser();

    }
}