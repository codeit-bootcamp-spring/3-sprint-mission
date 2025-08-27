package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component("messageSecurityService")
public class MessageSecurityService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageSecurityService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public boolean canEdit(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) return false;

        User user = userRepository .findById(userId).orElse(null);
        if (user == null) return false;

        // 작성자인 경우
        return message.getAuthor().getId().equals(user.getId());
    }

    public boolean canDelete(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) return false;

        User user = userRepository .findById(userId).orElse(null);
        if (user == null) return false;

        // 작성자인 경우
        return message.getAuthor().getId().equals(user.getId());
    }
}
