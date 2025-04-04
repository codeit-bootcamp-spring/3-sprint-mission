package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {

//        new User("이주용", "SB001", "B001", "sb001@gmail.com");
//        new User("황지인", "SB002", "B002", "sb002@gmail.com");
//        new User("백은호", "SB003", "B003", "sb003@gmail.com");
//        new User("조현아", "SB004", "B004", "sb004@gmail.comw");
//        new User("정윤지", "SB005", "B005", "sb005@gmail.com");

        //----------------------User----------------
        List<User> userList = new ArrayList<>();
        UserService userService = new JCFUserService(userList);

        //1.등록
        userService.create(new User("이주용", "SB001", "B001", "sb001@gmail.com"));
        userService.create(new User("황지인", "SB002", "B002", "sb002@gmail.com"));
        userService.create(new User("백은호", "SB003", "B003", "sb003@gmail.com"));
        userService.create(new User("조현아", "SB004", "B004", "sb004@gmail.comw"));
        userService.create(new User("정윤지", "SB005", "B005", "sb005@gmail.com"));

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
        List<Channel> channelList = new ArrayList<>();
        ChannelService channelService = new JCFChannelService(channelList);
        User LJY = new User("이주용", "SB001", "B001", "sb001@gmail.com");
        User HJI = new User("황지인", "SB002", "B002", "sb002@gmail.com");
        User BEH = new User("백은호", "SB003", "B003", "sb003@gmail.com");
        User JHA = new User("조현아", "SB004", "B004", "sb004@gmail.comw");
        User JYJ = new User("정윤지", "SB005", "B005", "sb005@gmail.com");

        //1.등록
        channelService.create(new Channel("스프린트 스프링 3기", "스프링 백엔드",false, LJY));
        channelService.create(new Channel("스프린트 커뮤니티", "스프링 백엔드",false, HJI));
        channelService.create(new Channel("롤 다인큐", "스프링 백엔드",false, BEH));
        channelService.create(new Channel("야놀자", "스프링 백엔드",false, JHA));
        channelService.create(new Channel("점메추", "스프링 백엔드",false, JYJ));

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
    }
}
