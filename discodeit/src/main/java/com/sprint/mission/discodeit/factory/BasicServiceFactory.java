package com.sprint.mission.discodeit.factory;


import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

// Repository 로부터 Service 객체를 만들어 관리하는 Factory
@Configuration
public class BasicServiceFactory implements ServiceFactory {
    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;
    private ReadStatusRepository readStatusRepository;
    private UserStatusRepository userStatusRepository;
    private BinaryContentRepository binaryContentRepository;

    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;



    public BasicServiceFactory(@Qualifier("userFilePath") Path userPath,
                               @Qualifier("channelFilePath") Path channelPath,
                               @Qualifier("messageFilePath") Path messagePath,
                               @Qualifier("readStatusFilePath") Path readStatusPath,
                               @Qualifier("userStatusFilePath") Path userStatusPath,
                               @Qualifier("binaryContentFilePath") Path binaryContentPath)
    {
        // Repository 초기화
        this.userRepository = new FileUserRepository(userPath);
        this.channelRepository = new FileChannelRepository(channelPath);
        this.messageRepository = new FileMessageRepository(messagePath);
        this.readStatusRepository = new FileReadStatusRepository(readStatusPath);
        this.userStatusRepository = new FileUserStatusRepository(userStatusPath);
        this.binaryContentRepository = new FileBinaryContentRepository(binaryContentPath);


        // Service 초기화
        this.userService = new BasicUserService(userRepository,userStatusRepository,binaryContentRepository);
        this.messageService = new BasicMessageService(messageRepository,channelRepository,userRepository,binaryContentRepository);
        this.channelService = new BasicChannelService(channelRepository,messageRepository,readStatusRepository,userRepository);
    }

    @Override
    public UserService createUserService() {
        if(userService == null){
            userService = new BasicUserService(userRepository,userStatusRepository,binaryContentRepository);
        }
        return userService;
    }

    @Override
    public ChannelService createChannelService() {
        if(channelService == null){
            channelService = new BasicChannelService(channelRepository,messageRepository,readStatusRepository,userRepository);
        }
        return channelService;
    }

    @Override
    public MessageService createMessageService() {
        if(messageService == null){
            messageService = new BasicMessageService(messageRepository,channelRepository,userRepository,binaryContentRepository);
        }
        return messageService;
    }


}
