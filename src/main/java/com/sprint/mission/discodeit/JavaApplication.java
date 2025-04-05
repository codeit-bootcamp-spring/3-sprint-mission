package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
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
 * 2025. 4. 5.        doungukkim       message의 map 결과 확인할 수 있게 수정
 * 2025. 4. 5.        doungukkim       심화 코드 작성, 결과 추가
 */
public class JavaApplication {
    private static final UserService userService;
    public static final MessageService messageService;
    public static ChannelService channelService;

    static {
        // 1. 구현체 먼저 생성
        JCFChannelService jcfChannelService = new JCFChannelService();
        JCFUserService jcfUserService = new JCFUserService(jcfChannelService);
        JCFMessageService jcfMessageService = new JCFMessageService(jcfChannelService);

        // 2. 순환 의존 setter로 해결
        jcfChannelService.setMessageService(jcfMessageService);

        // 3. 인터페이스로 노출
        channelService = jcfChannelService;
        messageService = jcfMessageService;
        userService = jcfUserService;
    }

    public static void main(String[] args) {

        System.out.println("-----------------------------------User 테스트 시작-----------------------------------");
//        등록 + id 변수에 저장
        System.out.println("**등록");
        UUID danielId = userService.registerUser("daniel");
        UUID johnId = userService.registerUser("john");
        UUID jackId = userService.registerUser("jack");
        UUID paulId = userService.registerUser("paul");

//        단건 조회
        System.out.println("**단건 조회(john)");
        userService.findUserById(johnId).stream().forEach(user -> System.out.println("["+user.getUsername()+"]"));

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
        System.out.println(userService.findUserById(jackId)
                .stream()
                .map(user -> user.getUsername())
                .collect(Collectors.toList()));

        System.out.print("\n**수정 확인(jack)\n수정 전: [daniel, john, jack, paul]\n수정 후: ");
        System.out.println(userService.findAllUsers().stream()
                .map(user -> user.getUsername())
                .collect(Collectors.joining(", ","[","]")));

//        삭제
        System.out.println("\n**삭제(paul)");
        userService.deleteUser(paulId);
//        삭제 확인
        System.out.println("삭제 확인");
        System.out.println(userService.findAllUsers().stream()
                .map(user -> user.getUsername())
                .collect(Collectors.joining(", ","[","]")));


        System.out.println("\n\n-----------------------------------Channel 테스트 시작-----------------------------------");

        // 체널에 등록될 유저(User에서 만든) 객체
        List<User> userDaniel = userService.findUserById(danielId);
        List<User> userHannah = userService.findUserById(hannahId);
        List<User> userJohn = userService.findUserById(johnId);

//        등록
        System.out.println("\n**채널 등록");
        UUID danielChannelId = channelService.createChannel(userDaniel);
        UUID hannahChannelId = channelService.createChannel(userHannah);
        UUID johnChannelId = channelService.createChannel(userJohn);

//        단건 조회
        System.out.println("\n**채널 단건 조회(danielChannelId)");
        channelService.findChannelsById(danielChannelId).stream()
                .map(channel -> channel.getTitle())
                .forEach(channel-> System.out.println("방 이름: "+channel));

//        다건 조회
        System.out.println("\n**채널 다건 조회");
        channelService.findAllChannel().stream()
                .map(channel -> channel.getTitle())
                .forEach(channel-> System.out.println("방 이름: "+channel));

//        수정
        System.out.println("\n**채널 이름 수정(daniel's channel => daniel's family channel)");
        channelService.updateChannelName(danielChannelId,"daniel's family channel");

//        수정 데이터 조회
        System.out.println("\n**수정 데이터 조회");
        System.out.println("변경 전: daniel's channel");
        System.out.println("변경 후: "+channelService.findChannelsById(danielChannelId).get(0).getTitle());

//        삭제, 삭제 확인
        System.out.println("\n**채널 삭제, 삭제 확인");
        System.out.print("삭제 전:");
        String roomTitles = channelService.findAllChannel().stream()
                .map(channel -> channel.getTitle())
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(roomTitles);
        channelService.deleteChannel(hannahChannelId);

        System.out.print("삭제 후:");
        roomTitles= channelService.findAllChannel().stream()
                .map(channel -> channel.getTitle())
                .collect(Collectors.joining(", ","[","]"));
        System.out.println(roomTitles);

        System.out.println("\n\n-----------------------------------Message 테스트 시작-----------------------------------");

//        등록
        // 방에 있는 유저의 아이디
        System.out.println("\n**메세지 추가");
        UUID danielMessageId = messageService.createMessage(danielId, danielChannelId, "hello, I am Daniel");
        UUID danielMessageId2 = messageService.createMessage(danielId, danielChannelId, "my favorite sport is baseball!");
        UUID hannahMessageId = messageService.createMessage(hannahId, hannahChannelId, "What are you doing?");

//        단건 조회(message)
        System.out.println("\n**메세지 단건 조회(daniel)");
        messageService.findMessageByMessageId(danielMessageId)
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).get(0).getUsername() + " : " + message.getMessage()));

//        다건 조회()
        System.out.println("\n**메세지 다건 조회");
        messageService.findAllMessages().stream()
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).get(0).getUsername() + " : " + message.getMessage()));

//        수정(message id기준)
        System.out.println("\n**메세지 수정");
        messageService.updateMessage(danielMessageId,"where is my project?");

//        수정된 데이터 조회(message)
        System.out.println("**수정 메세지 조회");
        System.out.println("수정 전 => daniel : hello, I am Daniel");
        messageService.findMessageByMessageId(danielMessageId)
                .forEach(message -> System.out.println("수정 후 => "+userService.findUserById(message.getSenderId()).get(0).getUsername() + " : " + message.getMessage()));

//        삭제
        System.out.println("\n**메세지 삭제(hannah : What are you doing?))");
        messageService.deleteMessageById(hannahMessageId);

//        삭제 확인
        System.out.println("삭제 확인(다건 조회)");
        messageService.findMessageByMessageId(hannahMessageId)
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).get(0).getUsername() + " : " + message.getMessage()));

        messageService.findAllMessages().stream()
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).get(0).getUsername() + " : " + message.getMessage()));



        System.out.println("\n\n-----------------------------------심화 테스트-----------------------------------");
        System.out.println("\n**시나리오 1 : message등록 => channel에 메세지 등록");


        // 체널에 메세지 추가
        System.out.println("\n1. message 추가: \"I hope this works fine.. please\"  ");
        UUID testMessageId = messageService.createMessage(danielId, danielChannelId, "I hope this works fine.. please");
        System.out.println("2. channel의 메세지 확인");
        channelService.findChannelsById(danielChannelId).get(0)
                .getMessages()
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).get(0).getUsername() + " : " + message.getMessage()));

        System.out.println("\n**시나리오 2 : message삭제 => channel의 메세지 삭제");
        System.out.println("\n1. 삭제 전 채널 메세지");
        // 채널의 메세지 확인
        channelService.findChannelsById(danielChannelId).get(0)
                .getMessages()
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).get(0).getUsername() + " : " + message.getMessage()));
        System.out.println("2. 메세지 삭제");
        messageService.deleteMessageById(testMessageId);
        System.out.println("3. 삭제 후 채널 메세지");
        channelService.findChannelsById(danielChannelId).get(0)
                .getMessages()
                .forEach(message -> System.out.println(userService.findUserById(message.getSenderId()).get(0).getUsername() + " : " + message.getMessage()));

        System.out.println("\n**시나리오 3 : channel삭제 => channel의 messages 삭제");
        System.out.println("\n1. 채널 삭제");
        channelService.deleteChannel(danielChannelId);

        List<Channel> channelResult = channelService.findChannelsById(danielChannelId);
        List<Message> messageResult = messageService.findMessageByMessageId(danielMessageId);

        System.out.println("2. 삭제한 체널과 메세지 조회");
        System.out.println("채널 조회 결과 : "+channelResult);
        System.out.println("메세지 조회 결과 : "+messageResult);

    }

//    테스트
//    [ ] 등록
//    [ ] 조회(단건, 다건)
//    [ ] 수정
//    [ ] 수정된 데이터 조회
//    [ ] 삭제
//    [ ] 조회를 통해 삭제되었는지 확인



}
