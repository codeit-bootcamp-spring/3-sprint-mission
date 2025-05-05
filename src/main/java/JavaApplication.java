import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
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
        messageRepository, userRepository, channelRepository // 수정됨
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
        messageRepository, userRepository, channelRepository // 수정됨
    );

    runTest(userService, channelService, messageService);
  }

  public static void runTest(UserService userService, ChannelService channelService,
      MessageService messageService) {
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
    printAfterDelete(userService, channelService, messageService, user.getId(), channel.getId(),
        message.getId());

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
