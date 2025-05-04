package com.sprint.mission.discodeit;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class JavaApplication2 {

    static User setupUser(BasicUserService basicUserService) {
        UserRepository userRepository;
        // 테스트용 유저 객체 생성
        List<User> userList = new ArrayList<>();
        User user1 = basicUserService.createUser("111111-1234567", "김첨지", 49, "cheomji@gmail.com");
        userList.add(user1);
        User user2 = basicUserService.createUser("222222-2234567", "김복순", 49, "luckysoon@gmail.com");
        userList.add(user2);

        return user1;
    }

    public static void main(String[] args) {

        // 저장 로직 호출
        UserRepository userRepository = new FileUserRepository();
//        UserRepository userRepository2 = new JCFUserRepository();

        // 서비스 초기화

        BasicUserService basicUserService = new BasicUserService(userRepository);

        // 셋업
        User user = setupUser(basicUserService);

        // 유저 조회(단건)
        System.out.println("-----------------(유저 조회)-----------------");

        basicUserService.findById((user.getId()));

        // 유저 조회(다건)
        System.out.println("-----------------(전체 유저 조회)-----------------");

        // 유저 정보 수정
        System.out.println("-----------------(유저 정보 수정)-----------------");

        // 유저 삭제
        System.out.println("-----------------(유저 삭제)-----------------");
    }
}
