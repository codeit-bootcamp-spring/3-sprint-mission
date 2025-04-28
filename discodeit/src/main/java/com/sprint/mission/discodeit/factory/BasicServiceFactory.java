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

    private BasicServiceFactory() {

    }

    public BasicServiceFactory(@Qualifier("userFilePath") Path userPath,
                               @Qualifier("channelFilePath") Path channelPath,
                               @Qualifier("messageFilePath") Path messagePath)
    {
        this.userRepository = new FileUserRepository(userPath);
        this.channelRepository = new FileChannelRepository(channelPath);
        this.messageRepository = new FileMessageRepository(messagePath);

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
