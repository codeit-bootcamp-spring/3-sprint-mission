import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        System.out.println("=== TEST JCF REPOSITORY ===");
        testWithJCF();
        System.out.println();

        System.out.println("=== TEST FILE REPOSITORY ===");
        testWithFile();
        System.out.println();
    }

    // Test with JCF-based repositories
    public static void testWithJCF() {
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);

        runTestScenario(userService, channelService, messageService);
    }

    // Test with File-based repositories
    public static void testWithFile() {
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);

        runTestScenario(userService, channelService, messageService);
    }

    // Common test flow
    public static void runTestScenario(UserService userService, ChannelService channelService, MessageService messageService) {
        // Create
        User user = userService.create("woody");
        Channel channel = channelService.create("general");
        Message message = messageService.create(user.getId(), channel.getId(), "hi i am woodyyyy");

        System.out.println("Created user: " + user.getUsername());
        System.out.println("Created channel: " + channel.getName());
        System.out.println("Created message: " + message.getContent());

        // Read
        User foundUser = userService.findById(user.getId());
        Channel foundChannel = channelService.findById(channel.getId());
        Message foundMessage = messageService.findById(message.getId());

        System.out.println("Found user: " + foundUser.getUsername());
        System.out.println("Found channel: " + foundChannel.getName());
        System.out.println("Found message: " + foundMessage.getContent());

        // Update
        userService.update(user.getId(), "buzz");
        channelService.update(channel.getId(), "notice");
        messageService.update(message.getId(), "Updated message content.");

        System.out.println("Updated user: " + userService.findById(user.getId()).getUsername());
        System.out.println("Updated channel: " + channelService.findById(channel.getId()).getName());
        System.out.println("Updated message: " + messageService.findById(message.getId()).getContent());

        // Delete
        userService.delete(user.getId());
        channelService.delete(channel.getId());
        messageService.delete(message.getId());

        System.out.println("After deletion:");
        System.out.println("User: " + userService.findById(user.getId()));
        System.out.println("Channel: " + channelService.findById(channel.getId()));
        System.out.println("Message: " + messageService.findById(message.getId()));

        // List all users/messages
        List<User> users = userService.findAll();
        List<Message> messages = messageService.findAll();

        System.out.println("All users: " + users.size());
        System.out.println("All messages: " + messages.size());
    }
}
