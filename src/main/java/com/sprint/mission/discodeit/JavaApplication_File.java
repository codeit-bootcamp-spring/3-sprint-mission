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

import java.util.List;
import java.util.Random;

// [V] JCF*Service 구현체와 File*Service 구현체를 비교하여 공통점과 차이점을 발견해보세요
    // 공통점
        // 동일한 도메인 인터페이스 구현
        // CRUD 메서드를 통한 동일한 작업 수행

    // 차이점
        // 데이터 저장 방식( JCF / File )
        // 데이터 수명 ( JCF : Application 실행 동안 유지 / File : 잔존 )
        // 설계 복잡도 ( JCF : 자바 내장 기능 이용 / File : 객체 (역)직렬화 처리 필요 )


    // [v] 비즈니스 로직 관련 코드 식별
        // create 메서드, update 메서드

    // [v] 저장 로직 관련 코드 식별
        // save 메서드( file 저장 )  /  JCF : put() 기능

// sprint2 test
public class JavaApplication_File {
    public static void main(String[] args) {

        // 서비스 구현체 변경 ( JCF >> File )
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        // ==================================================================================================================
        // Create 테스트

        // 유저 활동 상태
        Random random = new Random();

        // 유저 필드 : userName, pwd, email, phoneNumber, statusMessage, activityStatus, friends
        User user = new User("GeonMin",
                "q1w2e3r4", "GeonMin02@codeIt.com",
                "010-1564-8516", "CodeItBootCamp",
                random.nextBoolean(), System.currentTimeMillis());

        userService.createUser(user);

        //=================================================================
        // 채널 필드 : channelName, channelType, category, thread
        Channel channel = new Channel("Study",
                "Text", "study", System.currentTimeMillis());

        channelService.createChannel(channel);


        // ==========================================================================
        // 메세지 필드 : messageContent, messageType, fromUser, toChannel ( 발신자, 전송 채널, 내용, 유형 )
        User fromUser = userService.readUser(user.getUserId());
        List<Channel> toChannel = channelService.readChannelByName("Study");
        Message message = new Message("Hello, World!",
                "Text", fromUser, toChannel, System.currentTimeMillis());

        messageService.createMessage(message);


        // =====================================================================
        // Read 테스트( 유저 ID, 채널타입, 전체 메세지 )
        userService.readUser(user.getUserId());
        channelService.readChannelByType(channel.getChannelType());
        messageService.readAllMessages()
                .forEach(System.out::println);

        // Update ( 유저 pwd 변경 )
        user.setPwd("example");
        userService.updateUser(user.getUserId(), user);

        // Delete
        boolean isDeleted = messageService.deleteMessage(message.getMessageId());
        System.out.println("Deleted Message? " + isDeleted);

        // 삭제 후 목록 확인
        System.out.println("Message List After Delete : ");
        messageService.readAllMessages().forEach(System.out::println);



        // 출력 로그
        System.out.println();
        System.out.println();
        System.out.println("Created User: " + user);
        System.out.println("Created Channel: " + channel);
        System.out.println("Created Message: " + message);

        System.out.println("Read Message List:");
        messageService.readAllMessages().forEach(System.out::println);

        System.out.println("Updated User: " + userService.readUser(user.getUserId()));

        System.out.println("Deleted Message Exist? " + messageService.deleteMessage(message.getMessageId()));
        System.out.println();
        System.out.println();


        // Create
        messageService.createMessage(message);

        // 로그 확인
        System.out.println("Created Message ID: " + message.getMessageId());

        // Read All
        System.out.println("== Message List After Create ==");
        messageService.readAllMessages().forEach(m -> {
            System.out.println("Saved Message ID: " + m.getMessageId());
        });

        // Delete
        boolean isDeleted2 = messageService.deleteMessage(message.getMessageId());
        System.out.println("Deleted? " + isDeleted2);

    }
}