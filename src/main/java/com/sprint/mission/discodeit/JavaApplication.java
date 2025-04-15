package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * packageName    : com.sprint.mission.discodeit
 * fileName       : JavaApplication
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    : 결과 확인
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 * 2025. 4. 5.        doungukkim       message의 map 결과 할 수 있게 수정
 * 2025. 4. 5.        doungukkim       심화 코드 작성, 결과 추가
 */
public class JavaApplication {
    private static final UserService userService;
    public static final MessageService messageService;
    public static ChannelService channelService;

    static {
        // 1. 구현체 먼저 생성
        FileChannelService fileChannelService = new FileChannelService();
        FileUserService fileUserService = new FileUserService();
        FileMessageService fileMessageService = new FileMessageService(fileChannelService);
        // 2. 순환 의존 setter
        fileChannelService.setService(fileMessageService, fileUserService);

        // 3. 인터페이스로 노출
        channelService = fileChannelService;
        messageService = fileMessageService;
        userService = fileUserService;
    }

    public static void main(String[] args) {

        System.out.println("-----------------------------------User 테스트 시작-----------------------------------");
//        등록
        System.out.println("**등록");
        UUID danielId = userService.registerUser("daniel");
        UUID johnId = userService.registerUser("john");
        UUID jackId = userService.registerUser("jack");
        UUID paulId = userService.registerUser("paul");

//        단건 조회
        System.out.println("**단건 조회(john)");
        System.out.println("["+userService.findUserById(johnId).getUsername()+"]");

//        다건 조회
        System.out.println("\n**다건 조회");
        String joinedUsers = userService.findAllUsers().stream()
                .map(user -> user.getUsername())
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(joinedUsers);

//        수정
        System.out.println("\n**이름 수정(jack -> hannah)");
        userService.updateUsername(jackId,"hannah");
        UUID hannahId = jackId;

//        수정 데이터 조회
        System.out.println("**수정 데이터 조회");
        System.out.print("변경 전 이름: [jack]\n변경 후 이름: ");
        System.out.println("["+userService.findUserById(jackId).getUsername()+"]");

        System.out.print("\n**수정 확인(jack)\n수정 전: [daniel, john, jack, paul]\n수정 후: ");
        System.out.println(userService.findAllUsers().stream()
                .map(user -> user.getUsername())
                .collect(Collectors.joining(", ","[","]")));

//        삭제
        System.out.println("\n**삭제(paul)");
        userService.deleteUser(paulId);
//        삭제 확인
        System.out.println("**삭제 확인");
        System.out.println("유저 조회 결과: "+userService.findUserById(paulId));


        System.out.println("\n\n-----------------------------------Channel 테스트 시작-----------------------------------");

//        등록
        System.out.println("\n**채널 등록");
        UUID danielChannelId = channelService.createChannel(danielId);
        UUID hannahChannelId = channelService.createChannel(hannahId);
        channelService.createChannel(johnId);

//        단건 조회
        System.out.println("**채널 단건 조회(danielChannelId)");
        System.out.println("방 이름: " + channelService.findChannelById(danielChannelId).getTitle());

//        다건 조회
        System.out.println("\n**채널 다건 조회");
        channelService.findAllChannel().stream()
                .map(channel -> channel.getTitle())
                .forEach(channel-> System.out.println("방 이름: "+channel));

//        수정
        System.out.println("\n**채널 이름 수정(daniel's channel => daniel's family channel)");
        channelService.updateChannelName(danielChannelId,"daniel's family channel");

//        수정 데이터 조회
        System.out.println("**수정 데이터 조회");
        System.out.println("변경 후: "+channelService.findChannelById(danielChannelId).getTitle());

//        삭제, 삭제 확인
        System.out.println("\n**채널 삭제(hannah's channel)");
        channelService.deleteChannel(hannahChannelId);
        System.out.println("**삭제 확인");
        System.out.println("채널 조회 결과: "+channelService.findChannelById(hannahChannelId));


        System.out.println("\n\n-----------------------------------Message 테스트 시작-----------------------------------");

//        등록
        // 방에 있는 유저의 아이디
        System.out.println("\n**메세지 추가");
        UUID danielMessageId = messageService.createMessage(danielId, danielChannelId, "hello, I am Daniel");
        UUID hannahMessageId = messageService.createMessage(hannahId, hannahChannelId, "What are you doing?");
        messageService.createMessage(danielId, danielChannelId, "my favorite sport is baseball!");

//        단건 조회(message)
        System.out.println("**메세지 단건 조회(daniel)");

        Message msg = messageService.findMessageByMessageId(danielMessageId);
        System.out.println(userService.findUserById(msg.getSenderId()).getUsername() + " : " + msg.getMessage());

//        다건 조회()
        System.out.println("\n**메세지 다건 조회");
        messageService.findAllMessages().stream()
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));

//        수정(message id기준)
        System.out.println("\n**메세지 수정(hello, I am Daniel => where is my project?)");
        messageService.updateMessage(danielMessageId,"where is my project?");

//        수정된 데이터 조회(message)
        System.out.println("**수정 메세지 조회");
        msg = messageService.findMessageByMessageId(danielMessageId);
        System.out.println(userService.findUserById(msg.getSenderId()).getUsername() + " : " + msg.getMessage());

//        삭제
        System.out.println("\n**메세지 삭제(What are you doing?))");
        messageService.deleteMessageById(hannahMessageId);

//        삭제 확인
        System.out.println("**삭제 확인");
        System.out.println("메세지 조회 결과: " + messageService.findMessageByMessageId(hannahMessageId));

        System.out.println("\n\n-----------------------------------심화 테스트-----------------------------------");

        System.out.println("\n**시나리오 1 : message등록 => channel에 메세지 등록");
        // 체널에 메세지 추가
        System.out.println("\n1. message 추가: \"I hope this works fine.. please\"  ");
        UUID testMessageId = messageService.createMessage(danielId, danielChannelId, "I hope this works fine.. please");
        System.out.println("2. channel의 메세지 확인");
        channelService.findChannelById(danielChannelId)
                .getMessages()
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));


        System.out.println("\n**시나리오 2 : message삭제 => channel의 메세지 삭제");
        System.out.println("1. 메세지 삭제(daniel : I hope this works fine.. please)");
        messageService.deleteMessageById(testMessageId);
        System.out.println("2. 삭제 후 채널 메세지");
        channelService.findChannelById(danielChannelId)
                .getMessages()
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).getUsername() + " : " + message.getMessage()));

        System.out.println("\n**시나리오 3 : channel추가 => User에 channel 추가");
        System.out.println("\n**체널 추가(daniel's channel)");
        channelService.createChannel(danielId);
        System.out.println("**채널 조회 결과: ");
        channelService.findChannelsByUserId(danielId).stream().forEach(channel -> System.out.println(channel.getTitle()));
        System.out.println("**유저 객체에서 채널 조회 결과: ");
        userService.findChannelIdsInId(danielId).stream().forEach(channelId-> System.out.println("체널 id: "+channelId));


        System.out.println("\n**시나리오 4 : channel삭제 => channel의 messages 삭제");
        System.out.println("\n1. 채널 삭제");
        channelService.deleteChannel(danielChannelId);

        Channel channelResult = channelService.findChannelById(danielChannelId);
        Message messageResult = messageService.findMessageByMessageId(danielMessageId);

        System.out.println("2. 삭제한 체널과 메세지 조회");
        System.out.println("채널 조회 결과 : "+channelResult);
        System.out.println("메세지 조회 결과 : "+messageResult);
    }
}
