package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class TestUser {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();

        System.out.println("✅ [USER 테스트]");

        // 생성
        User user = userService.create("김민준");
        System.out.println("생성된 유저: " + user);

        // 단건 조회
        User found = userService.findById(user.getId());
        System.out.println("조회된 유저: " + found);

        // 수정
        userService.update(user.getId(), "민준김");
        System.out.println("수정된 유저 이름: " + userService.findById(user.getId()));

        // 전체 조회
        System.out.println("전체 유저 목록: " + userService.findAll());

        // 삭제
        userService.delete(user.getId());
        System.out.println("삭제 후 조회: " + userService.findById(user.getId()));
    }
}