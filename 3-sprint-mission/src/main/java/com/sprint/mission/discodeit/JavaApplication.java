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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {


        //----------------------User----------------
        //유저 생성
        User LJY = new User("이주용", "SB001", "B001", "sb001@gmail.com");
        User HJI = new User("황지인", "SB002", "B002", "sb002@gmail.com");
        User BEH = new User("백은호", "SB003", "B003", "sb003@gmail.com");
        User JHA = new User("조현아", "SB004", "B004", "sb004@gmail.comw");
        User JYJ = new User("정윤지", "SB005", "B005", "sb005@gmail.com");

        List<User> userList = new ArrayList<>();
        UserService userService = new JCFUserService(userList);

        //1.등록
        userService.create(LJY);
        userService.create(HJI);
        userService.create(BEH);
        userService.create(JHA);
        userService.create(JYJ);

        //2-1.전체 사용자 조회
        System.out.println("----------전체 사용자 조회----------");
        userService.readAll().forEach(System.out::println);
        System.out.println();

        //2-2.특정 사용자 조회
        System.out.println("----------특정 사용자 조회----------");
        userService.readById("SB001").forEach(System.out::println);
        System.out.println();

        //3.사용자 정보 수정
        System.out.println("----------사용자 정보 수정----------");
        System.out.print("수정 전 사용자 정보: ");
        userService.readById("SB005").forEach(System.out::println);
        userService.update("SB005", "장주현", "SB006", "B006", "sb006@gmail.com");
        String ModifiedUserId = "SB006";
        System.out.println();

        //4. 수정된 데이터 조회
        System.out.println("----------수정된 데이터 조회----------");
        System.out.print("수정 후 사용자 정보: ");
        userService.readById(ModifiedUserId).forEach(System.out::println);
        System.out.println();


        //5. 삭제
        System.out.println("----------데이터 삭제----------");
        System.out.print("삭제 할 사용자 정보: ");
        userService.readById("SB006").forEach(System.out::println);
        userService.deleteById("SB006");
        System.out.println();


        //6. 삭제 후 조회
        System.out.println("----------삭제 후 데이터 조회----------");
        userService.readAll().forEach(System.out::println);
        System.out.println();

        //----------------------Channel----------------------
        // 채널 생성
        Channel spring = new Channel("스프린트 스프링 3기", "스프링 백엔드", false, LJY);
        Channel community = new Channel("스프린트 커뮤니티", "스프링 백엔드", false, HJI);
        Channel lol = new Channel("롤 다인큐", "스프링 백엔드", false, BEH);
        Channel yanolja = new Channel("야놀자", "스프링 백엔드", false, JHA);
        Channel lunch = new Channel("점메추", "스프링 백엔드", false, JYJ);

        List<Channel> channelList = new ArrayList<>();
        ChannelService channelService = new JCFChannelService(channelList);

        //1.등록
        channelService.create(spring);
        channelService.create(community);
        channelService.create(lol);
        channelService.create(yanolja);
        channelService.create(lunch);

        //2-1.전체 채널 조회
        System.out.println("----------전체 채널 조회----------");
        channelService.readAll().forEach(System.out::println);
        System.out.println();


        //2-2.특정 사용자 조회
        System.out.println("----------특정 채널 조회----------");
        channelService.readById("스프린트 스프링 3기").forEach(System.out::println);
        System.out.println();

        //3.사용자 정보 수정
        System.out.println("----------채널 정보 수정----------");
        System.out.print("수정 전 채널 정보: ");
        channelService.readById("롤 다인큐").forEach(System.out::println);
        channelService.update("롤 다인큐", "롤 듀오", "영웅호걸", true);
        System.out.println();

        //4. 수정된 데이터 조회
        System.out.println("----------수정된 데이터 조회----------");
        System.out.print("수정 후 사용자 정보: ");
        channelService.readById("롤 듀오").forEach(System.out::println);
        System.out.println();

        //5. 삭제
        System.out.println("----------채널 삭제----------");
        System.out.print("삭제 할 채널 정보: ");
        channelService.readById("야놀자").forEach(System.out::println);
        channelService.deleteById("야놀자");
        System.out.println();


        //6. 삭제 후 조회
        System.out.println("----------채널 삭제 후 데이터 조회----------");
        channelService.readAll().forEach(System.out::println);
        System.out.println();

        //----------------------Message----------------------
        // 메세지 생성
        Message message1 = new Message(LJY, spring, "위워크 금욜 ㄱㄱ");
        Message message2 = new Message(HJI, spring, "ㅇㅋㅇㅋ");
        Message message3 = new Message(BEH, lol, "미드");
        Message message4 = new Message(JHA, yanolja, "예약 완료");
        Message message5 = new Message(JYJ, spring, "ㅇㅋ");
        Message message6 = new Message(LJY, lol, "정글");


        List<Message> messageList = new ArrayList<>();
        MessageService messageService = new JCFMessageService(messageList);

        //1.등록
        messageService.create(message1);
        messageService.create(message2);
        messageService.create(message3);
        messageService.create(message4);
        messageService.create(message5);
        messageService.create(message6);

        //2-1.전체 메세지 조회
        System.out.println("----------전체 메세지 조회----------");
        messageService.readAll().forEach(System.out::println);
        System.out.println();


        //2-2.특정 채널의 메세지 조회
        System.out.println("----------특정 채널 조회----------");
        messageService.readByChannelId(lol.getId()).forEach(System.out::println);
        System.out.println();

        //3.메세지 정보 수정
        System.out.println("----------메세지 내용 수정----------");
        System.out.print("수정 전 메세지 내용: ");
        messageService.readById(message6.getId()).forEach(System.out::println);
        messageService.update(message6.getId(), "아니다 탑 감");
        System.out.println();

        //4. 수정된 데이터 조회
        System.out.println("----------수정된 내용 조회----------");
        System.out.print("수정 후 메세지 내용: ");
        messageService.readById(message6.getId()).forEach(System.out::println);
        System.out.println();

        //5. 삭제
        System.out.println("----------메세지 삭제----------");
        System.out.print("삭제 할 메세지 정보: ");
        messageService.readById(message4.getId()).forEach(System.out::println);
        messageService.deleteById(message4.getId());
        System.out.println();


        //6. 삭제 후 조회
        System.out.println("----------메세지 삭제 후 데이터 조회----------");
        messageService.readAll().forEach(System.out::println);
        System.out.println();
    }
}
