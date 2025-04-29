package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JavaApplication {
    public static void main(String[] args) {
        ChannelService jcfChannelService = new JCFChannelService(new CopyOnWriteArrayList<>());
        UserService jcfUserService = new JCFUserService(new CopyOnWriteArrayList<>());
        MessageService jcfMessageService = new JCFMessageService(new CopyOnWriteArrayList<>(),jcfUserService,jcfChannelService);

        // ============== JCFChannelService 테스트 ==============
        System.out.println("============== JCFChannelService 테스트 ==============");
        System.out.println();

        // 등록
        Channel channel1 = new Channel("채널A", null);
        jcfChannelService.create(channel1);
        jcfChannelService.create(new Channel("채널B",null));
        jcfChannelService.create(new Channel("채널C",null));

        //조회(단건)
        System.out.println("============== 조회(단건) 테스트 ==============");
        jcfChannelService.readById(channel1.getId());
        System.out.println();

        //조회(다건)
        System.out.println("============== 조회(다건) 테스트 ==============");
        jcfChannelService.readAll();
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        Channel updateChannel = new Channel("수정채널", null);
        jcfChannelService.update(channel1.getId(),updateChannel);

        //수정된 데이터 조회
        jcfChannelService.readById(channel1.getId());
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        jcfChannelService.delete(channel1);

        //조회를통해 삭제되었는지 확인
        jcfChannelService.readAll();
        System.out.println();

        // ============== JCFUserService 테스트 ==============
        System.out.println("============== JCFUserService 테스트 ==============");
        System.out.println();
        //등록
        User user1 = new User("강문구","aaa","asd@qwe.com","010-0000-0000","다른용무중",false,false,null);
        User user2 = new User("코드잇","bbb","codeit@qwe.com","010-1111-2222","온라인",true,false,null);
        User user3 = new User("스프린트","ccc","sprint@qwe.com","010-1234-5678","자리비움",false,true,null);

        jcfUserService.create(user1);
        jcfUserService.create(user2);
        jcfUserService.create(user3);

        //조회(단건)
        System.out.println("============== 조회(단건) 테스트 ==============");
        jcfUserService.readById(user1.getId());
        System.out.println();

        //조회(다건)
        System.out.println("============== 조회(다건) 테스트 ==============");
        jcfUserService.readAll();
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        jcfUserService.update(user1.getId(),new User("스프링","spring","spring@naver.com","010-9999-9999","온라인",true,true,null));

        //수정된 데이터 조회
        jcfUserService.readById(user1.getId());
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        jcfUserService.delete(user1);

        //조회를통해 삭제되었는지 확인
        jcfUserService.readAll();
        System.out.println();

        // ============== 심화요구사항 및 JCFMessageService 테스트 ==============
        System.out.println("============== 심화요구사항 및 JCFChannelService 테스트 ==============");
        System.out.println();
        List<User> channel1Users = new ArrayList<>();
        channel1Users.add(user1);
        channel1Users.add(user2);
        channel1Users.add(user3);
        channel1.updateMembers(channel1Users);
        //등록
        Message message1 = new Message(null,null,"안녕하세요!");
        Message message2 = new Message("(웃는 이모티콘)",null,null);
        Message message3 = new Message(null,"(풍경사진)",null);
        jcfMessageService.create(message1,user1,channel1);
        jcfMessageService.create(message2,user2,channel1);
        jcfMessageService.create(message3,user3,channel1);

        //조회(단건)
        System.out.println("============== 조회(단건) 테스트 ==============");
        jcfMessageService.readById(message1.getId());
        System.out.println();

        //조회(다건)
        System.out.println("============== 조회(다건) 테스트 ==============");
        jcfMessageService.readAll();
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        jcfMessageService.update(message1.getId(),new Message(null,null,"안녕하세요 수정"));

        //수정된 데이터 조회
        jcfMessageService.readById(message1.getId());
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        jcfMessageService.delete(message1);

        //조회를통해 삭제되었는지 확인
        jcfMessageService.readAll();
        System.out.println();



    }
}
