package com.sprint.mission.discodeit.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.sprint.mission.discodeit.service.jcf.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;


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
        System.out.println("로그인 하십시오.");
        User currentuser = userService.login();
        System.out.println("현재 사용자: " + currentuser.getUserId() + "(로그인상태: " + currentuser.getIsLogin() + ")");

        // Update user information (수정)
        userService.update(currentuser);
        System.out.println("변경사항 확인");
        System.out.println(userService.read(currentuser.getUserId()).toString());

        // Delete user (삭제)
        System.out.println("삭제할 사용자를 입력해주십시오.");
        System.out.println("삭제할 ID: ");
        try {
            userService.delete(userService.read(reader.readLine()));
        } catch (NoSuchElementException e) {
            System.out.println("존재하지 않는 사용자입니다.");
        }
        userService.readAll().forEach(System.out::println);
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
        channelService.readAll(currentuser).forEach(System.out::println);

        channelService.create(currentuser);
        channelService.readAll(currentuser).forEach(System.out::println);

        channelService.create(currentuser);
        // Show all channels (다건 조회)
        channelService.readAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");

        // Show first channel (단건 조회)
        System.out.println("첫번째 채팅방 조회");
        System.out.println(channelService.read(currentuser, channelService.readAll(currentuser).get(0).getId()).toString());
        System.out.println("=======================");

        // Search channel by channel name (단건 조회)
        System.out.println("채팅방 이름으로 검색");
        try {
            channelService.readByName(currentuser, reader.readLine()).forEach(System.out::println);
        } catch (NoSuchElementException e) {
            System.out.println("해당 채팅방이 존재하지 않습니다.");
        }
        System.out.println("=======================");

        // Update channel information (수정)
        System.out.println("변경할 채팅방 선택");
        String name = reader.readLine();
        try {
            channelService.updateName(currentuser, channelService.readByName(currentuser, name).get(0).getId());
        } catch (NoSuchElementException e) {
            System.out.println("해당 채팅방이 존재하지 않습니다.");
        }

        // Check updated channel information (수정된 데이터 조회)
        channelService.readAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");

        // Remove second channel
        System.out.println("채팅방 삭제");
        System.out.println("삭제할 채팅방 선택");
        try {
            channelService.delete(currentuser, channelService.readByName(currentuser, reader.readLine()).get(0).getId());
        } catch (IllegalArgumentException e) {
            System.out.println("해당 채팅방이 존재하지 않습니다.");
        }
        System.out.println("=======================");

        // Show all users after deletion
        System.out.println("삭제 후 채팅방 목록 확인");
        channelService.readAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");

        //// 3) Message 도메인
        // 채팅방 선택
        System.out.println("어느 채팅방에 입장하시겠습니까?");
        channelService.readAll(currentuser).forEach(System.out::println);
        System.out.println("=======================");
        String channelname = reader.readLine();
        Channel channel = channelService.readByName(currentuser, channelname).get(0);
        System.out.println(channel.getName() + " 입장!");

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
                messageService.create(input, currentuser, channel);
                System.out.println(messageService.read(messageService.readAll().get(messageService.readAll().size()-1).getId()).toString());
            }
        }
        System.out.println("----------------------");
        System.out.println("여기까지 읽으셨습니다.");

        // Search message by text (단건 조회)
        System.out.println("메시지 검색");
        String searchWord = reader.readLine();
        messageService.readByText(searchWord).forEach(System.out::println);
        System.out.println("=======================");

        // Search message by sender name (단건 조회)
        System.out.println("보낸 사람 검색");
        String sender = reader.readLine();
        try {
            messageService.readBySender(sender).forEach(System.out::println);
            System.out.println("=======================");
        } catch (NoSuchElementException e) {
            System.out.println("해당 사용자가 존재하지 않습니다.");
        }
        System.out.println("=======================");

        // Update message (수정)
        System.out.println("마지막 메시지 변경");
        messageService.update(messageService.readAll().get(messageService.readAll().size()-1).getId(),reader.readLine());

        // Check updated message (수정된 데이터 조회)
        messageService.read(messageService.readAll().get(messageService.readAll().size()-1).getId());
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
                messageService.create(input, currentuser, channel);
                System.out.println(messageService.read(messageService.readAll().get(messageService.readAll().size()-1).getId()).toString());
            }
        }
        System.out.println("----------------------");
        System.out.println("여기까지 읽으셨습니다.");

        // Show all messages (다건 조회)
        System.out.println("메시지 전체 조회");
        messageService.readAll().forEach(System.out::println);
        System.out.println("=======================");

        // Remove second message
        System.out.println("마지막 메시지 삭제");
        messageService.delete(messageService.readAll().get(messageService.readAll().size()-1).getId());
        System.out.println("=======================");

        // Show all users after deletion
        System.out.println("삭제 후 전체 메시지 확인");
        messageService.readAll().forEach(System.out::println);
        System.out.println("=======================");
    }

}

