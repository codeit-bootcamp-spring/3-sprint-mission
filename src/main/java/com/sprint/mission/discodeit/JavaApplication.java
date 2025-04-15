package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


 // 도메인 별 서비스 구현체를 테스트
 // 등록, 조회(단건, 다건), 수정, 수정된 데이터 조회, 삭제, 조회를 통해 삭제되었는지 확인
 // [V] User CRUD
 // [ ] Channel CRUD
 // [ ] Message CURD
public class JavaApplication {
    public static void main(String[] args) {

        // 활동 상태 무작위 표시
        Random random = new Random();

        System.out.println();
        System.out.println("<<< || ================== 사용자 ( User ) CRUD 테스팅 ============================ || >>>");
        System.out.println();

        // ================== 사용자 ( User ) CRUD 테스팅 ============================

        // 유저 서비스 인스턴스 생성
        JCFUserService userService = new JCFUserService(new HashMap<>());

        // 1. 등록 ( Create )

        // 인스턴스 생성
        // userName, pwd, email, phoneNumber, statusMessage, activityStatus, friends
        // 초안
        User user = new User("Alice",
                "awesomepwd", "test@codeIt.com",
                "010-1234-5678", "hi",
                random.nextBoolean(), System.currentTimeMillis());

        // 인스턴스 추가 생성
        User user2 = new User("Bob","strongpwd123", "bob@example.com", "010-0000-1111", "have a nice day", random.nextBoolean(), System.currentTimeMillis());
        User user3 = new User("Alice", "ppwwdd", "she@codeit.com", "010-9876-5432", "Hello, world!", random.nextBoolean(), System.currentTimeMillis());
        User user4 = new User("Lucky", "777luck777", "Lucky@example.com", "010-7777-7777", "Lucky Day", random.nextBoolean(), System.currentTimeMillis());
        User user5 = new User("Siri", "anjfkwjrdjdigkwl", "AI_Siri@example.com", "010-0010-1101", "AI", random.nextBoolean(), System.currentTimeMillis());



        // 사용자 생성
        userService.createUser(user);       // Alice
        userService.createUser(user2);      // Bob
        userService.createUser(user3);      // Alice
        userService.createUser(user4);      // Lucky
        userService.createUser(user5);      // Siri
        System.out.println("=======================================================");
        System.out.println("[ 등록된 사용자 ] : " + String.join(" | ",        // 구분자 작업
                user.getUserName(), user2.getUserName(), user3.getUserName(), user4.getUserName(), user5.getUserName()));
        System.out.println();


// ======================

        // 2. 조회( 단건, 다건 )
        // 단일 조회
        User readUser = userService.readUser(user.getUserId());
        User readUser2 = userService.readUser(user2.getUserId());
        User readUser3 = userService.readUser(user3.getUserId());
        User readUser4 = userService.readUser(user4.getUserId());
        User readUser5 = userService.readUser(user5.getUserId());

        if (readUser2 == null) {     // 방어 코드 작성
            System.out.println(" ! 해당 사용자 정보를 찾을 수 없습니다 ! ");
            return;
        } else {        // 특정 사용자가 존재하면 정보 조회
            System.out.println("=======================================================");
            System.out.println("[ 조회한 사용자 ] : " + readUser2.getUserName());
            System.out.println(readUser2.getUserName() + "의 정보");
            System.out.println("[ 사용자 식별 코드 ] : " + readUser2.getUserId());
            System.out.println("[ 사용자 생성일자 ] : " + user2.getFormattedCreatedAt());      // 타임 스탬프
            System.out.println("[ PassWord ] : " + readUser2.getPwd());
            System.out.println("[ Email ] : " + readUser2.getEmail());
            System.out.println("[ PhoneNumber ] : " + readUser2.getPhoneNumber());
            System.out.println("[ 상태 메세지 ] : " + readUser2.getStatusMessage());
            if (readUser2.isActiveStatus()) {
                System.out.println("[ 활동 상태 ] : Online");
            } else {
                System.out.println("[ 활동 상태 ] : Offline");
            }
            System.out.println("[ 최종 수정일 ] : None");
            System.out.println();
        }

        // 다중 조회
        List<User> userByName = userService.readUserByName("Alice");
        System.out.println("=======================================================");
        System.out.println("[ 이름이 Alice인 사용자들 ] : " + userByName.size()+ "명");
        System.out.println();

        // 전체 조회
        System.out.println("=======================================================");
        System.out.println("[ 전체 사용자 목록 ] : " + userService.readAllUsers().size() + "명");
        userService.readAllUsers().forEach(allUser -> System.out.println(allUser.getUserName()));
        System.out.println();

// ========================

        // 3. 수정

        // email 수정
        String lastEmail = readUser.getEmail();                         // 변경점 확인용
        user3.setEmail("alice@codeIt.com");
        userService.updateUser(user3.getUserId(), user3);

// ========================

        // 4. 수정 후 조회
        System.out.println("=======================================================");
        System.out.println("[ 수정 후 사용자 조회 ]");
        System.out.println();
        System.out.println("( 사용자 " + readUser3.getUserName() + "의 변경점 ) : " + lastEmail + " -> " + userService.readUser(user3.getUserId()).getEmail());
        System.out.println("[ 정보 수정일 ] : " + user3.getFormattedUpdatedAt());               // 타임스탬프 달라짐
        System.out.println();

// ========================

        // 5. 사용자 삭제
        userService.deleteUser(user2.getUserId());          // Bob 삭제
        System.out.println("=======================================================");
        System.out.println("[ 사용자 삭제 후 목록 ]");
        userService.readAllUsers().forEach(allUser ->System.out.println(allUser.getUserName()));
        System.out.println();
        System.out.println();
        System.out.println();





        // ================== 채널 ( Channel ) CRUD 테스팅 ============================

        System.out.println("<<< || ================== 채널 ( Channel ) CRUD 테스팅 ============================ || >>>");
        System.out.println();

        // 채널 서비스 인스턴스 생성
        JCFChannelService channelService = new JCFChannelService(new HashMap<>());

        // 1. 등록 ( Create )
        // 필드 : channelName, channelType, category, thread
        Channel channel = new Channel("CodeIt", "Text", "study", System.currentTimeMillis());
        Channel channel2 = new Channel("BootCamp", "Voice", "study", System.currentTimeMillis());
        Channel channel3 = new Channel("Picture", "Text", "habby", System.currentTimeMillis());
        Channel channel4 = new Channel("Climbing", "Text", "sport", System.currentTimeMillis());
        Channel channel5 = new Channel("Prayer", "Voice", "Religion", System.currentTimeMillis());

        // 채널 생성
        channelService.createChannel(channel);
        channelService.createChannel(channel2);
        channelService.createChannel(channel3);
        channelService.createChannel(channel4);
        channelService.createChannel(channel5);
        System.out.println("=======================================================");
        System.out.println("[ 등록된 채널 ] : " + String.join(" | ",        // 구분자 작업
                channel.getChannelName(), channel2.getChannelName(), channel3.getChannelName(), channel4.getChannelName(), channel5.getChannelName()));
        System.out.println();

        // 채널에 인원 배정
        channel.addMember(user);
        channel.addMember(user3);
        channel.addMember(user4);           // 채널 1의 사용자 3명
        channel3.addMember(user5);          // 채널 3의 사용자 1명
        channel5.addMember(user);
        channel5.addMember(user5);          // 채널 5의 사용자 2명
        channel.removeMember(user3);         // 채널 1의 사용자 1명 제거 ( 3명 >> 2명 )

// ================================================================

        // 2. 조회 ( 단건, 다건 )
        // 특정 채널 조회 ( 단건 )
        Channel readChannel = channelService.readChannel(channel.getChannelId());
        Channel readChannel2 = channelService.readChannel(channel2.getChannelId());
        Channel readChannel3 = channelService.readChannel(channel3.getChannelId());
        Channel readChannel4 = channelService.readChannel(channel4.getChannelId());
        Channel readChannel5 = channelService.readChannel(channel5.getChannelId());

        if (readChannel4 == null) {
            System.out.println(" ! 해당 채널은 존재하지 않습니다 ! ");
        } else {
            System.out.println("=========================================================");
            System.out.println("[ 조회한 채널 ]  : " + readChannel4.getChannelName());
            System.out.println("[ 채널 ID ] : " + readChannel4.getChannelId());
            System.out.println("[ 채널 생성일 ] : " + channel4.getFormattedCreatedAt());     // 타임 스탬프
            System.out.println("[ 채널 타입 ] : " + readChannel4.getChannelType());
            System.out.println("[ 채널 소분류 ] : " + readChannel4.getCategory());
            System.out.println("[ 채널에 속한 멤버 수 ] : " +  readChannel4.getMembers().size() + "명");
            System.out.println("[ 수정일 ] : None");
            System.out.println();
        }

        // 다중 조회( 채널 타입 )
        List<Channel> channelByType = channelService.readChannelByType("Text");
        System.out.println("=========================================================");
        System.out.println("[ 채널 타입이 " + readChannel.getChannelType() + "인 채널 ] : " + channelByType.size() + "개");                // 개수 출력
        channelService.readAllChannels().stream().                                                                                         // Stream API 이용, 주어진 값 "Text"에 해당하는 채널명 출력
                filter(t -> "Text".equals(t.getChannelType())).                                                                  // < 조회하고자하는 채널 타입 설정을 유동적으로 못하나..
                forEach(t -> {System.out.println("채널명 : " + t.getChannelName());});
        System.out.println();

        // 전체 조회
        System.out.println("=========================================================");
        System.out.println("[ 전체 채널 목록 ] : " + channelService.readAllChannels().size() + "개");
        channelService.readAllChannels().forEach(allChannel -> System.out.println(allChannel.getChannelName()));
        System.out.println();

// ==============================================================================

        // 수정
        String lastName = readChannel.getChannelName();        // 변경점 확인용
        channel.setChannelName("CodeItBootCamp");                                                    // 수정 : 채널명 수정  | Setter 이용해서 변경, channelService.update 로 변경점 적용
        channelService.updateChannel(channel2.getChannelId(), channel);

        // 수정된 데이터 조회
        System.out.println("=========================================================");
        System.out.println("( " + channel.getChannelName() + " 의 변경점 ) : " + lastName + " -> " + readChannel.getChannelName());
        System.out.println("[ 수정일 ] : " + channel.getFormattedUpdatedAt());         // 타임스탬프 변경됨
        System.out.println();


// ================================================================================

        // 삭제
        System.out.println("=========================================================");
        channelService.deleteChannel(channel2.getChannelId());      // BootCamp
        channelService.deleteChannel(channel4.getChannelId());      // Climbing
        System.out.println("[ 채널 삭제 후 채널 목록 ]");
        channelService.readAllChannels().forEach(allChannel -> System.out.println(allChannel.getChannelName()));
        System.out.println();
        System.out.println();
        System.out.println();





        // ================== 메세지 ( Message ) CRUD 테스팅 ============================

        System.out.println("<<< || ================== 메세지 ( Message ) CRUD 테스팅 ============================ || >>>");
        System.out.println();

        // 메세지 인스턴스 생성
        JCFMessageService messageService = new JCFMessageService(new HashMap<>());


        // 서브 인스턴스
        User fromUser = userService.readUser(user.getUserId());
        User fromUser3 = userService.readUser(user3.getUserId());
        User fromUser5 = userService.readUser(user5.getUserId());


        Channel toChannel = channelService.readChannelByName("CodeItBootCamp");
        Channel toChannel2 = channelService.readChannelByName("Prayer");


        // 1. 등록
        // 필드 : messageContent, messageType, fromUser, toChannel ( 발신자, 전송 채널, 내용, 유형 )
        Message message = new Message("Hello, World!", "Voice", fromUser3, toChannel, System.currentTimeMillis());          // 음성 메세지 (타입)
        Message message2 = new Message("Have a nice day!", "Text", fromUser5, toChannel2, System.currentTimeMillis());
        Message message3 = new Message("thx", "Text", fromUser, toChannel2, System.currentTimeMillis());

        messageService.createMessage(message);
        messageService.createMessage(message2);
        messageService.createMessage(message3);

        System.out.println("=========================================================");
        System.out.println("[ 등록된 메세지 ] : " + String.join(" | ",        // 구분자 작업
                message.getMessageContent(), message2.getMessageContent(), message3.getMessageContent()));
        System.out.println();

// =====================================================================================

        // 2. 조회 ( 단건, 다건 )
        // 단일 조회
        Message readMessage = messageService.readMessage(message.getMessageId());
        Message readMessage2 = messageService.readMessage(message2.getMessageId());
        Message readMessage3 = messageService.readMessage(message3.getMessageId());

        if (readMessage2 == null) {
            System.out.println(" ! 해당 채널은 존재하지 않습니다 ! ");
        } else {
            System.out.println("=========================================================");
            System.out.println("[ 조회한 메세지 ]  : " + readMessage2.getMessageContent());
            System.out.println("[ 메세지 ID ] : " + readMessage2.getMessageId());
            System.out.println("[ 메세지 발신자 ] : " + fromUser5.getUserName());                 // 발신자 ID >> 이름 변환
            System.out.println("[ 메세지 전송시각 ] : " + message2.getFormattedCreatedAt());     // 타임 스탬프
            System.out.println("[ 메세지 타입 ] : " + readMessage2.getMessageType());
            System.out.println("[ 수정일 ] : None");
            System.out.println();
        }

        // 다중 조회( 메세지 타입 : Message Type )
        List<Message> messageByType = messageService.readMessageByType("Text");
        System.out.println("=========================================================");
        System.out.println("[ 메세지  유형이 " + readMessage2.getMessageType() + "인 메세지 ] : " + messageByType.size() + "개");                // 개수 출력
        messageService.readAllMessages().stream()
                .filter(t -> "Text".equals(t.getMessageType()))
                .forEach(t -> {System.out.println("메세지 내용 : " + t.getMessageContent());});
        System.out.println();

        // 전체 조회
        System.out.println("=========================================================");
        System.out.println("[ 전체 메세지 목록 ] : " + messageService.readAllMessages().size() + "개");
        messageService.readAllMessages().forEach(allMessages -> System.out.println(allMessages.getMessageContent()));
        System.out.println();

// ==============================================================================

        // 수정
        String lastContent = readMessage3.getMessageContent();        // 변경점 확인용
        message3.setMessageContent("u too");
        messageService.updateMessage(message3.getMessageId(), message);

        // 수정된 데이터 조회
        System.out.println("=========================================================");
        System.out.println("( 메세지 내용 수정 ) : " + lastContent + " -> " + readMessage3.getMessageContent());
        System.out.println("[ 수정일 ] : " + message.getFormattedUpdatedAt());         // 타임스탬프 변경됨
        System.out.println();


// ================================================================================

        // 삭제
        System.out.println("=========================================================");
        messageService.deleteMessage(message3.getMessageId());
        System.out.println("[ 메세지 삭제 후 전체 메세지 로그 ]");
        messageService.readAllMessages().forEach(allMessage -> System.out.println(allMessage.getMessageContent()));
    }
}
