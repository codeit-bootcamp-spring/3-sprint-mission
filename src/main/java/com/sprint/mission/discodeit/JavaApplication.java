package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.extern.log4j.Log4j2;

import java.util.Comparator;
import java.util.List;

@Log4j2
public class JavaApplication {
    public static void main(String[] args) {

        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        UserService userService = serviceFactory.getUserService();
        ChannelService channelService = serviceFactory.getChannelService();
        MessageService messageService = serviceFactory.getMessageService();

        List<User> users = createUserManagement(userService);
        List<Channel> channels = createChannelManagement(channelService, users);
        createMessageManagement(messageService, users, channels);
    }

    private static List<User> createUserManagement(UserService userService) {
        log.info("----------사용자 등록----------");
        User person1 = userService.create("이주용", "SB001", "!qwe0001", "sb001@gmail.com");
        User person2 = userService.create("황지인", "SB002", "!qwe0002", "sb002@gmail.com");
        User person3 = userService.create("백은호", "SB003", "!qwe0003", "sb003@gmail.com");
        User person4 = userService.create("조현아", "SB004", "!qwe0004", "sb004@gmail.com");
        User person5 = userService.create("정윤지", "SB005", "!qwe0005", "sb005@gmail.com");
//        User person6 = userService.create("정윤지", "SB005", "!qwe0005", "sb005@gmail.com"); // 아이디 중복 검증
//        User person7 = userService.create("정윤지", "SB006", "1111", "sb005@gmail.com"); // 비밀번호 양식 검증
//        User person8 = userService.create("정윤지", "SB006", "!qwe0005", "sb005@gmail.com"); // 이메일 중복 검증

        log.info("{}님이 등록되었습니다", person1.getUserName());
        log.info("{}님이 등록되었습니다", person2.getUserName());
        log.info("{}님이 등록되었습니다", person3.getUserName());
        log.info("{}님이 등록되었습니다", person4.getUserName());
        log.info("{}님이 등록되었습니다", person5.getUserName());
        System.out.println();

        log.info("----------전체 사용자 조회----------");
        userService.readAll().forEach(u -> log.info("{}", u));

        log.info("----------특정 사용자 조회----------");
        log.info("{}", userService.readById(person1.getId()));

        log.info("----------사용자 정보 수정----------");
        User targetPerson = person5;
        log.info("수정 전 사용자 정보: {}", userService.readById(targetPerson.getId()));
        userService.update(targetPerson, "장주현", "SB006", "B006", "sb006@gmail.com");

        log.info("----------수정된 유저 정보 조회----------");
        log.info("수정 후 사용자 정보: {}", userService.readById(person5.getId()));

        log.info("----------유저 삭제----------");
        targetPerson = person4;
        log.info("삭제 할 사용자 정보: {}", userService.readById(targetPerson.getId()));
        userService.deleteById(targetPerson);

        log.info("----------삭제 후 전체 유저 조회----------");
        userService.readAll().forEach(u -> log.info("{}", u));

        return userService.readAll();

    }

    private static List<Channel> createChannelManagement(ChannelService channelService, List<User> userList) {

        log.info("----------채널 등록----------");
        Channel ch1 = channelService.create("스프린트 스프링 3기", "스프링 백엔드", false, userList.get(0), "");
        Channel ch2 = channelService.create("스프린트 커뮤니티", "스프링 백엔드", false, userList.get(1), "");
        Channel ch3 = channelService.create("롤 다인큐", "스프링 백엔드", true, userList.get(2), "1234");
        Channel ch4 = channelService.create("야놀자", "스프링 백엔드", false, userList.get(3), "");
        Channel ch5 = channelService.create("점메추", "스프링 백엔드", false, userList.get(0), "");

        log.info("{} 채널이 생성 되었습니다", ch1.getChannelName());
        log.info("{} 채널이 생성 되었습니다", ch2.getChannelName());
        log.info("{} 채널이 생성 되었습니다", ch3.getChannelName());
        log.info("{} 채널이 생성 되었습니다", ch4.getChannelName());
        log.info("{} 채널이 생성 되었습니다", ch5.getChannelName());
        System.out.println();

        log.info("----------전체 채널 조회----------");
        channelService.readAll().stream().sorted(Comparator.comparing(Channel::getCreatedAt)).forEach(c -> log.info("{}", c));

        log.info("----------특정 채널 조회----------");
        log.info("{}", channelService.readById(ch1.getId()));

        log.info("----------채널 정보 수정----------");
        Channel targetChannel = ch3;
        log.info("수정 전 채널 정보: {}", channelService.readById(targetChannel.getId()));
        channelService.update(targetChannel, "롤 듀오", "영웅호걸", true, "1234");

        log.info("----------수정된 채널 조회----------");
        log.info("수정 후 채널 정보: {}", channelService.readById(targetChannel.getId()));

        log.info("----------채널 삭제----------");
        targetChannel = ch4;
        log.info("삭제 할 채널 정보: {}", channelService.readById(targetChannel.getId()));
        channelService.deleteById(userList.get(3), targetChannel);

        log.info("----------채널 삭제 후 데이터 조회----------");
        channelService.readAll().forEach(c -> log.info("{}", c));

        log.info("----------채널 멤버 추가 ----------");
        channelService.joinChannel(userList.get(1), ch3, "1234");
        channelService.joinChannel(userList.get(0), ch3, "1234");
        log.info("총 {}명의 사용자가 채널에 있습니다.", ch3.getMemberList().size());
        ch3.getMemberList().forEach(u -> log.info("{}", u));
        System.out.println();

        log.info("----------채널 멤버 제거 ----------");
        channelService.leave(userList.get(1), ch3);
        System.out.println();

        return channelService.readAll();
    }

    private static void createMessageManagement(MessageService messageService, List<User> userList, List<Channel> channelList) {

        log.info("----------메세지 전송----------");
        Message message1 = messageService.create(userList.get(0), channelList.get(0), "위워크 금욜 ㄱㄱ");
        Message message2 = messageService.create(userList.get(1), channelList.get(0), "ㅇㅋㅇㅋ");
        Message message3 = messageService.create(userList.get(2), channelList.get(2), "미드");
        Message message4 = messageService.create(userList.get(2), channelList.get(0), "예약 완료");
        Message message5 = messageService.create(userList.get(3), channelList.get(0), "ㅇㅋ");
        Message message6 = messageService.create(userList.get(0), channelList.get(2), "정글");

        log.info("{} 채널에 {}님이 메세지를 보냈습니다.", message1.getChannel().getChannelName(), message1.getUser().getUserName());
        log.info("{} 채널에 {}님이 메세지를 보냈습니다.", message2.getChannel().getChannelName(), message2.getUser().getUserName());
        log.info("{} 채널에 {}님이 메세지를 보냈습니다.", message3.getChannel().getChannelName(), message3.getUser().getUserName());
        log.info("{} 채널에 {}님이 메세지를 보냈습니다.", message4.getChannel().getChannelName(), message4.getUser().getUserName());
        log.info("{} 채널에 {}님이 메세지를 보냈습니다.", message5.getChannel().getChannelName(), message5.getUser().getUserName());
        log.info("{} 채널에 {}님이 메세지를 보냈습니다.", message6.getChannel().getChannelName(), message6.getUser().getUserName());
        System.out.println();

        log.info("----------전체 메세지 조회----------"); //각 객체 리스트들을 LinkedHash로 변경하였기에 sorted는 삭제 해도 됨
        messageService.readAll().stream().sorted(Comparator.comparing(Message::getCreatedAt)).forEach(m -> log.info("{}", m));

        log.info("----------특정 채널 메세지 조회----------");
        messageService.readByChannelId(channelList.get(2).getId()).stream().sorted(Comparator.comparing(Message::getCreatedAt)).forEach(m -> log.info("{}", m));

        log.info("----------메세지 내용 수정----------");
        log.info("수정 전 메세지 내용: {}", messageService.readById(message6.getId()));
        messageService.update(message6.getId(), "아니다 탑 감");

        log.info("----------수정된 내용 조회----------");
        messageService.readByChannelId(message6.getChannel().getId()).forEach(e -> log.info("{}", e));

        log.info("----------메세지 삭제----------");
        log.info("삭제 할 메세지 정보: {}", messageService.readById(message4.getId()));
        messageService.deleteById(message4.getId());

        log.info("----------메세지 삭제 후 데이터 조회----------");
        messageService.readByChannelId(channelList.get(0).getId()).stream().sorted(Comparator.comparing(Message::getCreatedAt)).forEach(m -> log.info("{}", m));

//         log.info("유효성 검사 예외 발생 테스트");
//         messageService.create(person4,ch1,"삭제된 유저");
//         messageService.create(person1,ch4,"삭제된 채널");
    }
}
