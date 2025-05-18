package com.sprint.mission.discodeit.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;
import com.sprint.mission.discodeit.service.file.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;


public class JavaApplication {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService();
//        MessageService messageService = new JCFMessageService();
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        //// 1) User 도메인
        // Create users (등록)
        System.out.println("사용자 등록");
        userService.create();

        userService.findAll().forEach(System.out::println);

        System.out.println("사용자 등록");
        userService.create();

        userService.findAll().forEach(System.out::println);

        System.out.println("사용자 등록");
        userService.create();

        userService.findAll().forEach(System.out::println);

        // Show first user (단건 조회)
        System.out.println("사용자 ID로 검색");
        System.out.println("사용자 ID: ");
        String searchid = reader.readLine();
        try {
            System.out.println(userService.findByUserId(searchid).toString());
        } catch (NoSuchElementException e) {
            System.out.println("해당 사용자가 없습니다.");
        }
        System.out.println("=======================");

        // Search user by username (단건 조회)
        System.out.println("사용자 이름으로 검색");
        System.out.println("사용자 이름: ");
        String searchname = reader.readLine();
        userService.findByName(searchname).forEach(System.out::println);
        System.out.println("=======================");

        // Show all users (다건 조회)
        System.out.println("전체 조회");
        userService.findAll().forEach(System.out::println);;
        System.out.println("=======================");

        // update는 로그인 후 사용 가능
        System.out.println("로그인 하십시오.");
        User currentuser = userService.login();
        System.out.println("현재 사용자: " + currentuser.getUserId() + "(로그인상태: " + currentuser.getIsLogin() + ")");

        // Update user information (수정)
        userService.update(currentuser.getId());
        System.out.println("변경사항 확인");
        System.out.println(userService.find(currentuser.getId()).toString());

        // Delete user (삭제)
        System.out.println("삭제할 사용자를 입력해주십시오.");
        System.out.println("삭제할 ID: ");
        try {
            userService.delete(userService.findByUserId(reader.readLine()).getId());
        } catch (NoSuchElementException e) {
            System.out.println("존재하지 않는 사용자입니다.");
        }
        userService.findAll().forEach(System.out::println);
        System.out.println("=======================");

        // 로그아웃
        System.out.println("로그아웃합니다.");
        try {
            userService.logout(currentuser);
        } catch (NoSuchElementException e) {
            System.out.println("존재하지 않는 사용자입니다.");
        }

        // 로그인
        currentuser = userService.login();
        System.out.println("현재 사용자: " + currentuser.getUserId() + "(로그인상태: " + currentuser.getIsLogin() + ")");

        //// 2. Channel 도메인
        // Create channels (등록)
        channelService.create(currentuser);
        channelService.findAll(currentuser).forEach(System.out::println);

        channelService.create(currentuser);
        channelService.findAll(currentuser).forEach(System.out::println);

        channelService.create(currentuser);
        // Show all channels (다건 조회)
        channelService.findAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");

        // Show first channel (단건 조회)
        System.out.println("첫번째 채팅방 조회");
        System.out.println(channelService.find(currentuser, channelService.findAll(currentuser).get(0).getId()).toString());
        System.out.println("=======================");

        // Search channel by channel name (단건 조회)
        System.out.println("채팅방 이름으로 검색");
        try {
            channelService.findByName(currentuser, reader.readLine()).forEach(System.out::println);
        } catch (NoSuchElementException e) {
            System.out.println("해당 채팅방이 존재하지 않습니다.");
        }
        System.out.println("=======================");

        // Update channel information (수정)
        System.out.println("변경할 채팅방 선택");
        String name = reader.readLine();
        try {
            channelService.updateName(currentuser, channelService.findByName(currentuser, name).get(0).getId());
        } catch (NoSuchElementException e) {
            System.out.println("해당 채팅방이 존재하지 않습니다.");
        }

        // Check updated channel information (수정된 데이터 조회)
        channelService.findAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");

        // Remove second channel
        System.out.println("채팅방 삭제");
        System.out.println("삭제할 채팅방 선택");
        try {
            channelService.delete(currentuser, channelService.findByName(currentuser, reader.readLine()).get(0).getId());
        } catch (IllegalArgumentException e) {
            System.out.println("해당 채팅방이 존재하지 않습니다.");
        }
        System.out.println("=======================");

        // Show all users after deletion
        System.out.println("삭제 후 채팅방 목록 확인");
        channelService.findAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");

        //// 3) Message 도메인
        // 채팅방 선택
        System.out.println("어느 채팅방에 입장하시겠습니까?");
        channelService.findAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");
        String channelname = reader.readLine();
        Channel currentchannel = channelService.findByName(currentuser, channelname).get(0);
        System.out.println(currentchannel.getName() + " 입장!");

        System.out.println("------------------------");
        System.out.println("'" + currentuser.getName() + "' 님이 입장하셨습니다.");

        // Create messages (등록)
        System.out.println("(메시지 등록 종료: X)");
        System.out.println("-----------------------");
        boolean isEnded = false;
        while (!isEnded) {
            String input = reader.readLine();
            if (input.equalsIgnoreCase("X")) {
                isEnded = true;
            } else {
                messageService.create(currentuser, currentchannel, input);
                System.out.println(messageService.find(currentuser, currentchannel, messageService.readAll(currentuser, currentchannel).get(messageService.readAll(currentuser, currentchannel).size()-1).getId()).toString());
            }
        }
        System.out.println("----------------------");
        System.out.println("여기까지 읽으셨습니다.");

        // Search message by text (단건 조회)
        System.out.println("메시지 검색");
        String searchWord = reader.readLine();
        messageService.findByText(currentuser, currentchannel, searchWord).forEach(System.out::println);
        System.out.println("=======================");

        // Update message (수정)
        System.out.println("마지막 메시지 변경");
        messageService.update(currentuser, currentchannel, messageService.readAll(currentuser, currentchannel).get(messageService.readAll(currentuser, currentchannel).size()-1).getId(),reader.readLine());

        // Check updated message (수정된 데이터 조회)
        messageService.find(currentuser, currentchannel, messageService.readAll(currentuser, currentchannel).get(messageService.readAll(currentuser, currentchannel).size()-1).getId());
        System.out.println("=======================");

        // 메시지 등록
        System.out.println("(메시지 등록 종료: X)");
        System.out.println("-----------------------");
        isEnded = false;
        while (!isEnded) {
            String input = reader.readLine();
            if (input.equalsIgnoreCase("X")) {
                isEnded = true;
            } else {
                messageService.create(currentuser, currentchannel, input);
                System.out.println(messageService.find(currentuser, currentchannel, messageService.readAll(currentuser, currentchannel).get(messageService.readAll(currentuser, currentchannel).size()-1).getId()).toString());
            }
        }
        System.out.println("----------------------");
        System.out.println("여기까지 읽으셨습니다.");

        // Show all messages (다건 조회)
        System.out.println("메시지 전체 조회");
        messageService.readAll(currentuser, currentchannel).forEach(System.out::println);
        System.out.println("=======================");

        // Remove last message
        System.out.println("마지막 메시지 삭제");
        messageService.delete(currentuser, currentchannel, messageService.readAll(currentuser, currentchannel).get(messageService.readAll(currentuser, currentchannel).size()-1).getId());
        System.out.println("=======================");

        // Show all users after deletion
        System.out.println("삭제 후 전체 메시지 확인");
        messageService.readAll(currentuser, currentchannel).forEach(System.out::println);
        System.out.println("=======================");
    }

}

