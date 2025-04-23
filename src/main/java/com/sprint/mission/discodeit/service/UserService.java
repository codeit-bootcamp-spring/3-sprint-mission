package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserService {

   User createUser(String name);                     // Create 새로운 유저 생성

   void showAllUsers();                              // Read 전체 유저리스트 읽기
   User findUserByName(String name);                 // Read 이름으로 단일유저 찾기

   void updateUserName(User user,String name);       // Update 기존 유저 수정

   void deleteUser(String name);                     // Delete 이름으로 유저 삭제

   List<User> getUserslist();                        // 유저 리스트 반환
}
