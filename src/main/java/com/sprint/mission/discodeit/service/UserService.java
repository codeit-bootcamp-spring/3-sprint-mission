package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserService {

   User createUser(String name);                     // 새로운 유저 생성
   void updateUserName(User user,String name);        // 기존 유저 수정
   void showAllUsers();
   List<User> getUserslist();              // 유저 리스트 반환

   User findUserByName(String name); // 이름으로 단일유저 찾기
   boolean deleteUser(String name); // 이름으로 유저 삭제


//   User generateUser(String name);                     // 새로운 유저 생성
}
