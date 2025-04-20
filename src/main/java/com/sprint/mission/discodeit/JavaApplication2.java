import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.file.FileUserService;
import com.sprint.mission.discodeit.file.FileChannelService;
import com.sprint.mission.discodeit.file.FileMessageService;

public class JavaApplication2 {
    public static void main(String[] args) {
        // FileIO 기반 서비스 구현체로 교체
        UserService userService = new main.java.com.sprint.mission.discodeit.FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        // 테스트용 데이터
        User user = userService.create("김민준");
        Channel channel = channelService.create("일반채널");
        Message message = messageService.create("반가워요!", channel.getId(), user.getId()); // ✅ 괄호 마무리

        // 결과 출력
        System.out.println("👤 유저: " + user);
        System.out.println("📺 채널: " + channel);
        System.out.println("💬 메시지: " + message);
    }
}
