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
        User p1 = new User("이주용", "SB001", "B001", "sb001@gmail.com");
        User p2 = new User("황지인", "SB002", "B002", "sb002@gmail.com");
        User p3 = new User("백은호", "SB003", "B003", "sb003@gmail.com");
        User p4 = new User("조현아", "SB004", "B004", "sb004@gmail.comw");
        User p5 = new User("정윤지", "SB005", "B005", "sb005@gmail.com");
        //채널 생성
        Channel ch1 = new Channel("스프린트 스프링 3기", "스프링 백엔드", false, p1);
        Channel ch2 = new Channel("스프린트 커뮤니티", "스프링 백엔드", false, p2);
        Channel ch3 = new Channel("롤 다인큐", "스프링 백엔드", false, p3);
        Channel ch4 = new Channel("야놀자", "스프링 백엔드", false, p4);
        Channel ch5 = new Channel("점메추", "스프링 백엔드", false, p5);
        // 메세지 생성
        Message message1 = new Message(p1, ch1, "위워크 금욜 ㄱㄱ");
        Message message2 = new Message(p2, ch1, "ㅇㅋㅇㅋ");
        Message message3 = new Message(p3, ch3, "미드");
        Message message4 = new Message(p4, ch4, "예약 완료");
        Message message5 = new Message(p5, ch1, "ㅇㅋ");
        Message message6 = new Message(p1, ch3, "정글");


        List<User> userList = new ArrayList<>();
        UserService userService = new JCFUserService(userList);
        User targetPerson = p5;

        //1.등록
        userService.create(p1);
        userService.create(p2);
        userService.create(p3);
        userService.create(p4);
        userService.create(p5);

        //2-1.전체 사용자 조회
        System.out.println("----------전체 사용자 조회----------");
        userService.readAll().forEach(System.out::println);
        System.out.println();

        //2-2.특정 사용자 조회
        System.out.println("----------특정 사용자 조회----------");
        userService.readById(p1).forEach(System.out::println);
        System.out.println();

        //3.사용자 정보 수정
        System.out.println("----------사용자 정보 수정----------");
        System.out.print("수정 전 사용자 정보: ");
        userService.readById(targetPerson).forEach(System.out::println);
        userService.update(targetPerson, "장주현", "SB006", "B006", "sb006@gmail.com");
        System.out.println();

        //4. 수정된 데이터 조회
        System.out.println("----------수정된 데이터 조회----------");
        System.out.print("수정 후 사용자 정보: ");
        userService.readById(p5).forEach(System.out::println);
        System.out.println();


        //5. 삭제
        System.out.println("----------데이터 삭제----------");
        System.out.print("삭제 할 사용자 정보: ");
        targetPerson = p4;
        userService.readById(targetPerson).forEach(System.out::println);
        userService.deleteById(targetPerson);
        System.out.println();


        //6. 삭제 후 조회
        System.out.println("----------삭제 후 데이터 조회----------");
        userService.readAll().forEach(System.out::println);
        System.out.println();

        //----------------------Channel----------------------

        List<Channel> channelList = new ArrayList<>();
        ChannelService channelService = new JCFChannelService(channelList);
        Channel targetChannel = ch3;

        //1.등록
        channelService.create(ch1);
        channelService.create(ch2);
        channelService.create(ch3);
        channelService.create(ch4);
        channelService.create(ch5);

        //2-1.전체 채널 조회
        System.out.println("----------전체 채널 조회----------");
        channelService.readAll().forEach(System.out::println);
        System.out.println();


        //2-2.특정 사용자 조회
        System.out.println("----------특정 채널 조회----------");
        channelService.readById(ch1).forEach(System.out::println);
        System.out.println();

        //3.사용자 정보 수정
        System.out.println("----------채널 정보 수정----------");
        System.out.print("수정 전 채널 정보: ");
        channelService.readById(targetChannel).forEach(System.out::println);
        channelService.update(targetChannel, "롤 듀오", "영웅호걸", true);
        System.out.println();

        //4. 수정된 데이터 조회
        System.out.println("----------수정된 데이터 조회----------");
        System.out.print("수정 후 사용자 정보: ");
        channelService.readById(targetChannel).forEach(System.out::println);
        System.out.println();

        //5. 삭제
        System.out.println("----------채널 삭제----------");
        System.out.print("삭제 할 채널 정보: ");
        targetChannel = ch4;
        channelService.readById(targetChannel).forEach(System.out::println);
        channelService.deleteById(targetChannel);
        System.out.println();


        //6. 삭제 후 조회
        System.out.println("----------채널 삭제 후 데이터 조회----------");
        channelService.readAll().forEach(System.out::println);
        System.out.println();

        //----------------------Message----------------------

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
        messageService.readByChannelId(ch3.getId()).forEach(System.out::println);
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
