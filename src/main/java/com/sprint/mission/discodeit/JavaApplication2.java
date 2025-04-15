package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.List;

public class JavaApplication2 {

    static User setupUser(UserService userService) {
        User user = new User("woody", "woody1234", "woody@codeit.com","010-1111-1111", "다른용무중", false, false, null);
        userService.create(user);
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = new Channel("채널A", null);
        channelService.create(channel);
        return channel;
    }

    static Message messageCreateTest(MessageService messageService, Channel channel, User author) {
        List<User> channelUsers = new ArrayList<>();
        channelUsers.add(author);
        channel.updateMembers(channelUsers);
        String str = "반갑습니다"+channel.getId()+author.getId();
        Message message =new Message(null,null, str);
        messageService.create(message,author,channel);
        return message;
    }

    public static void main(String[] args) {
        // 서비스 초기화
        // TODO Basic*Service 구현체를 초기화하세요.
        ChannelRepository channelRepository = new JCFChannelRepository();
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageRepository messageRepository = new JCFMessageRepository();
        MessageService messageService = new BasicMessageService(messageRepository);
        UserRepository userRepository = new JCFUserRepository();
        UserService userService = new BasicUserService(userRepository);

        // ============== Basic *** Service 테스트 ==============
        System.out.println("============== Basic *** Service 테스트 ==============");
        System.out.println();

        // 등록
        Channel channel1 = setupChannel(channelService);
        User user1 = setupUser(userService);
        Message message1 = messageCreateTest(messageService,channel1,user1);
        Channel channela = setupChannel(channelService);
        User usera = setupUser(userService);
        Message messagea = messageCreateTest(messageService,channel1,user1);

        //조회(단건)
        System.out.println("============== 조회(단건) 테스트 ==============");
        System.out.println("CHANNEL");
        channelService.readById(channel1.getId());
        System.out.println();
        System.out.println("USER");
        userService.readById(user1.getId());
        System.out.println();
        System.out.println("MESSAGE");
        messageService.readById(message1.getId());
        System.out.println();

        //조회(다건)
        System.out.println("============== 조회(다건) 테스트 ==============");
        System.out.println("CHANNEL");
        channelService.readAll();
        System.out.println();

        System.out.println("USER");
        userService.readAll();
        System.out.println();

        System.out.println("MESSAGE");
        messageService.readAll();
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        Channel updateChannel = new Channel("수정채널", null);
        channelService.update(channel1.getId(),updateChannel);
        User updateUser = new User("사용자변경", "qweqwe","1111@gmail.com","010-0000-0000","오프라인",false,false,null);
        userService.update(user1.getId(),updateUser);
        Message updateMessage = new Message(null,null,"수정된텍스트입니다.");
        messageService.update(message1.getId(),updateMessage);


        //수정된 데이터 조회
        System.out.println("CHANNEL");
        channelService.readById(channel1.getId());
        System.out.println("USER");
        userService.readById(user1.getId());
        System.out.println("MESSAGE");
        messageService.readById(message1.getId());
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        channelService.delete(channel1);
        userService.delete(user1);
        messageService.delete(message1);

        //조회를통해 삭제되었는지 확인
        System.out.println("CHANNEL");
        channelService.readAll();
        System.out.println();

        System.out.println("USER");
        userService.readAll();
        System.out.println();

        System.out.println("MESSAGE");
        messageService.readAll();
        System.out.println();

        // ====================================================================================================
        // ==============  *** Service 테스트 ==============
        System.out.println("============== JCF *** Service 테스트 ==============");
        System.out.println();

        // 다형성을 이용한 인스턴스 변경
        channelService = new JCFChannelService(new ArrayList<>());
        userService = new JCFUserService(new ArrayList<>());
        messageService = new JCFMessageService(new ArrayList<>(),userService,channelService);

        // 등록
        Channel channel2 = setupChannel(channelService);
        User user2 = setupUser(userService);
        Message message2 = messageCreateTest(messageService,channel2,user2);
        Channel channelb = setupChannel(channelService);
        User userb = setupUser(userService);
        Message messageb = messageCreateTest(messageService,channel2,user2);

        //조회(단건)
        System.out.println("============== 조회(단건) 테스트 ==============");
        System.out.println("CHANNEL");
        channelService.readById(channel2.getId());
        System.out.println();
        System.out.println("USER");
        userService.readById(user2.getId());
        System.out.println();
        System.out.println("MESSAGE");
        messageService.readById(message2.getId());
        System.out.println();

        //조회(다건)
        System.out.println("============== 조회(다건) 테스트 ==============");
        System.out.println("CHANNEL");
        channelService.readAll();
        System.out.println();

        System.out.println("USER");
        userService.readAll();
        System.out.println();

        System.out.println("MESSAGE");
        messageService.readAll();
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        Channel updateChannel2 = new Channel("수정채널", null);
        channelService.update(channel2.getId(),updateChannel);
        User updateUser2 = new User("사용자변경", "qweqwe","1111@gmail.com","010-0000-0000","오프라인",false,false,null);
        userService.update(user2.getId(),updateUser);
        Message updateMessage2 = new Message(null,null,"수정된텍스트입니다.");
        messageService.update(message2.getId(),updateMessage);


        //수정된 데이터 조회
        System.out.println("CHANNEL");
        channelService.readById(channel2.getId());
        System.out.println("USER");
        userService.readById(user2.getId());
        System.out.println("MESSAGE");
        messageService.readById(message2.getId());
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        channelService.delete(channel2);
        userService.delete(user2);
        messageService.delete(message2);

        //조회를통해 삭제되었는지 확인
        System.out.println("CHANNEL");
        channelService.readAll();
        System.out.println();

        System.out.println("USER");
        userService.readAll();
        System.out.println();

        System.out.println("MESSAGE");
        messageService.readAll();
        System.out.println();



        // ====================================================================================================
        // ==============  *** Service 테스트 ==============
        System.out.println("============== File *** Service 테스트 ==============");
        System.out.println();

        // 다형성을 이용한 인스턴스 변경
        channelService = new FileChannelService();
        userService = new FileUserService();
        messageService = new FileMessageService();
        System.out.println();

        // 등록
        Channel channel3 = setupChannel(channelService);
        User user3 = setupUser(userService);
        Message message3 = messageCreateTest(messageService,channel1,user1);
        Channel channelc = setupChannel(channelService);
        User userc = setupUser(userService);
        Message messagec = messageCreateTest(messageService,channel1,user1);

        //조회(단건)
        System.out.println("============== 조회(단건) 테스트 ==============");
        System.out.println("CHANNEL");
        channelService.readById(channel3.getId());
        System.out.println();
        System.out.println("USER");
        userService.readById(user3.getId());
        System.out.println();
        System.out.println("MESSAGE");
        messageService.readById(message3.getId());
        System.out.println();

        //조회(다건)
        System.out.println("============== 조회(다건) 테스트 ==============");
        System.out.println("CHANNEL");
        channelService.readAll();
        System.out.println();

        System.out.println("USER");
        userService.readAll();
        System.out.println();

        System.out.println("MESSAGE");
        messageService.readAll();
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        Channel updateChannel3 = new Channel("수정채널", null);
        channelService.update(channel3.getId(),updateChannel);
        User updateUser3 = new User("사용자변경", "qweqwe","1111@gmail.com","010-0000-0000","오프라인",false,false,null);
        userService.update(user3.getId(),updateUser);
        Message updateMessage3 = new Message(null,null,"수정된텍스트입니다.");
        messageService.update(message3.getId(),updateMessage);


        //수정된 데이터 조회
        System.out.println("CHANNEL");
        channelService.readById(channel3.getId());
        System.out.println("USER");
        userService.readById(user3.getId());
        System.out.println("MESSAGE");
        messageService.readById(message3.getId());
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        channelService.delete(channel3);
        userService.delete(user3);
        messageService.delete(message3);

        //조회를통해 삭제되었는지 확인
        System.out.println("CHANNEL");
        channelService.readAll();
        System.out.println();

        System.out.println("USER");
        userService.readAll();
        System.out.println();

        System.out.println("MESSAGE");
        messageService.readAll();
        System.out.println();

    }



}
