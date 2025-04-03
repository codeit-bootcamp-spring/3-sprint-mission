package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.HashMap;

public class JavaApplication {
    public static void main(String[] args) {

/*
객체 별
[ ] 등록
[ ] 조회(단건, 다건)
[ ] 수정
[ ] 수정된 데이터 조회
[ ] 삭제
[ ] 조회를 통해 삭제되었는지 확인*/

        System.out.println("---service start---");
        /* user service */
        JCFUserService userService = new JCFUserService(new HashMap<>());

        // User 5명 생성 및 등록
        User user1 = new User("John", 20);
        User user2 = new User("John", 60);
        User user3 = new User("Bob", 25);
        User user4 = new User("Alice", 22);
        User user5 = new User("Charlie", 28);
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        userService.create(user4);
        userService.create(user5);


        // User 조회 - 전체
        userService.readAll().stream().forEach(user ->
                System.out.println(user.toString())
        );
        // User 조회 - by name
        userService.read("John").stream().forEach(user ->
                System.out.println(user.toString())
        );
        // User 조회 - by id
        System.out.println(userService.read(user4.getId()).toString());

        // User 수정
        User updatedUser = userService.update(user3.getId(), 3);
        System.out.println("updatedUser = " + updatedUser.toString());

        // User 삭제
        userService.delete(user2.getId());

        // User 삭제 후 결과 조회
        userService.readAll().stream().forEach(user ->
                System.out.println(user.toString())
        );


        System.out.println("---service end---");

    }
}
