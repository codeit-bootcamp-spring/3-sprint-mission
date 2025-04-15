import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        // 서비스 인스턴스 생성
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        // 등록
        User user = userService.create("chat");
        Channel channel = channelService.create("channel");
        Message message = messageService.create(user.getId(), channel.getId(), "hi hellooo");

        System.out.println("===== Registration Completed =====");
        System.out.println("User: " + user.getUsername());
        System.out.println("Channel: " + channel.getName());
        System.out.println("Message: " + message.getContent());
        System.out.println();

        // 단일 조회
        User foundUser = userService.findById(user.getId());
        Channel foundChannel = channelService.findById(channel.getId());
        Message foundMessage = messageService.findById(message.getId());

        System.out.println("===== Data Retrieved =====");
        System.out.println("User: " + foundUser.getUsername());
        System.out.println("Channel: " + foundChannel.getName());
        System.out.println("Message: " + foundMessage.getContent());
        System.out.println();

        // 수정
        userService.update(user.getId(), "update");
        channelService.update(channel.getId(), "chat channel");
        messageService.update(message.getId(), "updated message");

        // 수정 후 조회
        System.out.println("===== Updated Data =====");
        System.out.println("User: " + userService.findById(user.getId()).getUsername());
        System.out.println("Channel: " + channelService.findById(channel.getId()).getName());
        System.out.println("Message: " + messageService.findById(message.getId()).getContent());
        System.out.println();

        // 삭제
        userService.delete(user.getId());
        channelService.delete(channel.getId());
        messageService.delete(message.getId());

        // 삭제 확인
        System.out.println("===== Check After Deletion =====");
        System.out.println("User: " + userService.findById(user.getId()));       // null
        System.out.println("Channel: " + channelService.findById(channel.getId())); // null
        System.out.println("Message: " + messageService.findById(message.getId())); // null
        System.out.println();

        // ========== 여러 사용자와 메시지 등록 ==========
        Channel commonChannel = channelService.create("general");

        User user1 = userService.create("Yujin");
        User user2 = userService.create("Ginnie");
        User user3 = userService.create("Matilda");

        messageService.create(user1.getId(), commonChannel.getId(), "H i");
        messageService.create(user2.getId(), commonChannel.getId(), "H e l l o");
        messageService.create(user3.getId(), commonChannel.getId(), "H e y");

        // 사용자 목록 조회
        System.out.println("===== All Users =====");
        List<User> users = userService.findAll();
        for (User u : users) {
            System.out.println("- " + u.getUsername());
        }
        System.out.println();

        // 메시지 목록 조회
        System.out.println("===== All Messages =====");
        List<Message> messages = messageService.findAll();
        for (Message m : messages) {
            System.out.println("- From User ID: " + m.getUserId());
            System.out.println("  Content: " + m.getContent());
        }
        System.out.println();
    }
}
