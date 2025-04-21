import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("========= TEST: JCF REPOSITORY =========");
        testWithBasicAndJCF();

        System.out.println("\n========= TEST: FILE REPOSITORY =========");
        testWithBasicAndFile();
    }

    public static void testWithBasicAndJCF() {
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(
            messageRepository, userService, channelService
        );

        runTest(userService, channelService, messageService);
    }

    public static void testWithBasicAndFile() {
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(
            messageRepository, userService, channelService
        );

        runTest(userService, channelService, messageService);
    }

    public static void runTest(UserService userService, ChannelService channelService, MessageService messageService) {
        System.out.println("\n========== CREATE ==========");
        User user = userService.create("woody");
        Channel channel = channelService.create("general");
        Message message = messageService.create(user.getId(), channel.getId(), "hi i am woodyyyy");

        System.out.println("- User created: " + user.getUsername());
        System.out.println("- Channel created: " + channel.getName());
        System.out.println("- Message created: " + message.getContent());

        System.out.println("\n========== READ ==========");
        System.out.println("- User found: " + userService.findById(user.getId()).getUsername());
        System.out.println("- Channel found: " + channelService.findById(channel.getId()).getName());
        System.out.println("- Message found: " + messageService.findById(message.getId()).getContent());

        System.out.println("\n========== UPDATE ==========");
        User updatedUser = userService.update(user.getId(), "buzz");
        Channel updatedChannel = channelService.update(channel.getId(), "notice");
        Message updatedMessage = messageService.update(message.getId(), "Updated message content.");

        System.out.println("- User updated: " + updatedUser.getUsername());
        System.out.println("- Channel updated: " + updatedChannel.getName());
        System.out.println("- Message updated: " + updatedMessage.getContent());

        System.out.println("\n========== DELETE ==========");
        printAfterDelete(userService, channelService, messageService, user.getId(), channel.getId(), message.getId());

        System.out.println("========== FINAL COUNT ==========");
        System.out.println("- Total users: " + userService.findAll().size());
        System.out.println("- Total messages: " + messageService.findAll().size());
        System.out.println();
    }

    private static void printAfterDelete(
        UserService userService, ChannelService channelService, MessageService messageService,
        UUID userId, UUID channelId, UUID messageId
    ) {
        User deletedUser = userService.delete(userId);
        Channel deletedChannel = channelService.delete(channelId);
        Message deletedMessage = messageService.delete(messageId);

        System.out.println("- Deleted user: " +
            (deletedUser != null ? deletedUser.getUsername() : "Unknown") +
            " → now: " + userService.findById(userId));
        System.out.println("- Deleted channel: " +
            (deletedChannel != null ? deletedChannel.getName() : "Unknown") +
            " → now: " + channelService.findById(channelId));
        System.out.println("- Deleted message: " +
            (deletedMessage != null ? deletedMessage.getContent() : "Unknown") +
            " → now: " + messageService.findById(messageId));
        System.out.println();
    }
}
