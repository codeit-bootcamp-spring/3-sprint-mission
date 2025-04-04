package com.sprint.mission.discodeit;

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
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class JavaApplication {
    private static UserService userService = new JCFUserService();
    private static ChannelService channelService = new JCFChannelService();
    private static MessageService messageService = new JCFMessageService(channelService);


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

        System.out.println("\n**삭제(paul)");
        userService.deleteUser(paulId);
        System.out.println(userService.findAllUsers().stream()
                .map(user -> user.getUsername())
                .collect(Collectors.joining(", ","[","]")));


        System.out.println("\n\n-----------------------------------Channel 테스트 시작-----------------------------------");

        // 체널에 등록될 유저 객체
        List<User> userDaniel = userService.findUserById(danielId);
        List<User> userJack = userService.findUserById(jackId);
        List<User> userJohn = userService.findUserById(johnId);
        List<User> multipleUsers = List.of(
                userService.findUserById(johnId).get(0),
                userService.findUserById(danielId).get(0)
        );

//        등록
        System.out.println("\n**채널 등록");
        // channelId
        UUID danielChannel = channelService.createChannel(userDaniel);
        UUID MultipleUsersChannel = channelService.createChannel(multipleUsers);
        UUID hannahChannel = channelService.createChannel(userJack);
        UUID johnChannel = channelService.createChannel(userJohn);



//        단건 조회
        System.out.println("\n**채널 단건 조회(danielChannel)");
        channelService.findChannelsById(danielChannel).stream()
                .map(channel -> channel.getTitle())
                .forEach(channel-> System.out.println("방 이름: "+channel));


//        다건 조회
        System.out.println("\n**채널 다건 조회");
        channelService.findAllChannel().stream()
                .map(channel -> channel.getTitle())
                .forEach(channel-> System.out.println("방 이름: "+channel));

//        수정
        System.out.println("\n**채널 이름 수정(daniel's channel => daniel's family channel)");
        channelService.updateChannelName(danielChannel,"daniel's family channel");

//        수정 데이터 조회
        System.out.println("\n**수정 데이터 조회");
        System.out.println("변경 전: daniel's channel");
        System.out.println("변경 후: "+channelService.findChannelsById(danielChannel).get(0).getTitle());

//        삭제
        System.out.println("\n**채널 삭제");
        System.out.print("삭제 전:");
        String roomTitles = channelService.findAllChannel().stream()
                .map(channel -> channel.getTitle())
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(roomTitles);
        channelService.deleteChannel(hannahChannel);

        System.out.print("삭제 후:");
        roomTitles= channelService.findAllChannel().stream()
                .map(channel -> channel.getTitle())
                .collect(Collectors.joining(", ","[","]"));
        System.out.println(roomTitles);


        System.out.println("\n\n-----------------------------------Message 테스트 시작-----------------------------------");
//      메세지는  지정된 채팅방이 있어야 하고 최소 한명 이상의 유저가 필요하다.
//      메세지는 userid, channelid, message 필요
//      create 될 때 메세지가 channel에 추가되어야 한다.
//      exception 주어진 체널에 유저가 있어야 한다.

//        등록
        // 방에 있는 유저의 아이디
        System.out.println("\n**메세지 추가");
        UUID danielMessage = messageService.createMessage(danielId, danielChannel, "hello, I am Daniel");
        UUID johnMessage = messageService.createMessage(johnId, johnChannel, "What are you doing?");
        UUID multipleUsersMessage = messageService.createMessage(johnId, MultipleUsersChannel, "hello, people I am John");


//        단건 조회(message)
        System.out.println("\n**메세지 단건 조회(daniel)");
        messageService.findMessageById(danielMessage).
                forEach(m -> System.out.println(userService.findUserById(m.getUserId()).get(0).getUsername()+": "+m.getMessage()));


//        다건 조회()
        System.out.println("\n**메세지 다건 조회");
        messageService.findAllMessages()
                .forEach(m -> System.out.println(userService.findUserById(m.getUserId()).get(0).getUsername()+": "+m.getMessage()));

//        수정(message id기준)
        System.out.println("\n**메세지 수정");
        messageService.updateMessage(johnMessage,"Where is tom");

//        수정된 데이터 조회(message)
        System.out.println("**수정 메세지 조회");
        System.out.println("수정 전: What are you doing?");
        System.out.println(messageService.findMessageById(danielMessage)
                .stream()
                .map(e -> e.getMessage())
                .collect(Collectors.joining("] [", "수정 후: ", "")));


//    [ ] 삭제
        System.out.println("\n**메세지 삭제(Hello, people I am John))");
        System.out.print("삭제 전: ");

        System.out.println(messageService.findAllMessages().stream()
                .map(e -> e.getMessage())
                .collect(Collectors.joining("] [", "[", "]")));

        messageService.deleteMessageById(multipleUsersMessage);
        System.out.print("삭제 후: ");
        System.out.println(messageService.findAllMessages().stream()
                .map(e -> e.getMessage())
                .collect(Collectors.joining("] [", "[", "]")));



        System.out.println("\n\n-----------------------------------심화 과정 테스트-----------------------------------");
        System.out.println("\n**시나리오 1 : message삭제 => channel의 메세지 삭제");

        // 체널에 메세지 추가
        UUID testMessageId = messageService.createMessage(jackId, danielChannel, "I am testing deleteMessageById");
        UUID testMessageId2 = messageService.createMessage(danielId, danielChannel, "I hope this works fine.. please");

        System.out.println("\n삭제 전 채널: ["+channelService.findChannelsById(danielChannel).get(0).getTitle()+"]");
        // 채널의 메세지 확인
        channelService.findChannelsById(danielChannel).get(0).getMessages()
                .stream()
                .forEach(message -> System.out.println(userService.findUserById(message.getUserId()).get(0).getUsername()+" : "+ message.getMessage()));

        // 메세지 삭제
        messageService.deleteMessageById(testMessageId);

        // 채널의 메세지 확인
        System.out.println("\n삭제 후 채널: ["+channelService.findChannelsById(danielChannel).get(0).getTitle()+"]");
        channelService.findChannelsById(danielChannel).get(0).getMessages()
                .stream()
                .forEach(message -> System.out.println(userService.findUserById(message.getUserId()).get(0).getUsername()+" : "+ message.getMessage()));

//        System.out.println("\n**시나리오 2 : channel삭제 => channel의 messages 삭제");
//
//        System.out.println("\n삭제 예정 채널: ["+channelService.findChannelsById(danielChannel).get(0).getTitle()+"]");
//        channelService.findChannelsById(danielChannel).get(0).getMessages()
//                .stream()
//                .forEach(message -> System.out.println(userService.findUserById(message.getUserId()).get(0).getUsername()+" : "+ message.getMessage()));
//
//        channelService.deleteChannel(danielChannel);
//        messageService.findMessageById(testMessageId).forEach(message -> System.out.println("삭제한 채널의 메세지 조회결과: "+message.getMessage()));




    }

//    테스트
//    [ ] 등록
//    [ ] 조회(단건, 다건)
//    [ ] 수정
//    [ ] 수정된 데이터 조회
//    [ ] 삭제
//    [ ] 조회를 통해 삭제되었는지 확인



}
