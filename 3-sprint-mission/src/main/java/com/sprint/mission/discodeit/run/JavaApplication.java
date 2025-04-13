package com.sprint.mission.discodeit.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;


public class JavaApplication {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        //// 1) User 도메인
        // Create users (등록)
        System.out.println("사용자 등록");
        userService.create();

        userService.readAll().forEach(System.out::println);

        System.out.println("사용자 등록");
        userService.create();

        userService.readAll().forEach(System.out::println);

        // Show first user (단건 조회)
        System.out.println("사용자 ID로 검색");
        System.out.println("사용자 ID: ");
        String searchid = reader.readLine();
        try {
            System.out.println(userService.read(searchid).toString());
        } catch (NoSuchElementException e) {
            System.out.println("해당 사용자가 없습니다.");
        }
        System.out.println("=======================");

        // Search user by username (단건 조회)
        System.out.println("사용자 이름으로 검색");
        System.out.println("사용자 이름: ");
        String searchname = reader.readLine();
        userService.readByName(searchname).forEach(System.out::println);
        System.out.println("=======================");

        // Show all users (다건 조회)
        System.out.println("전체 조회");
        userService.readAll().forEach(System.out::println);;
        System.out.println("=======================");

        // update는 로그인 후 사용 가능
        System.out.println("로그인 하세요.");
        User currentuser = userService.login();
        System.out.println("현재 사용자: " + currentuser.getUserId() + "(로그인상태: " + currentuser.getIsLogin() + ")");

        // Update user information (수정)
        userService.update(currentuser);
        System.out.println("변경사항 확인");
        System.out.println(userService.read(currentuser.getUserId()).toString());

        //// 2. Channel 도메인
        // Create channels (등록)
        channelService.create(currentuser);
        channelService.readAll(currentuser).forEach(System.out::println);

        channelService.create(currentuser);
        channelService.readAll(currentuser).forEach(System.out::println);

        channelService.create(currentuser);
        channelService.readAll(currentuser).forEach(System.out::println);

        // Show all channels (다건 조회)
        channelService.readAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");

        // Show first channel (단건 조회)
        System.out.println("채팅방 선택");
        channelService.read(currentuser, channelService.readAll(currentuser).get(0).getId());
        System.out.println("=======================");

        // Search channel by channel name (단건 조회)
        System.out.println("채팅방 이름으로 검색");
        channelService.readByName(currentuser, "4조").forEach(System.out::println);
        System.out.println("=======================");

        // Update channel information (수정)
        System.out.println("첫번째 채팅방의 이름 변경");
        channelService.updateName(currentuser, channelService.readAll(currentuser).get(0).getId());

        // Check updated channel information (수정된 데이터 조회)
        channelService.read(currentuser, channelService.readAll(currentuser).get(0).getId());
        System.out.println("=======================");

        // Remove second channel
        System.out.println("채팅방 삭제");
        channelService.delete(currentuser, channelService.readByName(currentuser, "잡담").get(0).getId());
        System.out.println("=======================");

        // Show all users after deletion
        System.out.println("삭제 후 채팅방 목록 확인");
        channelService.readAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");

        
    }

}

