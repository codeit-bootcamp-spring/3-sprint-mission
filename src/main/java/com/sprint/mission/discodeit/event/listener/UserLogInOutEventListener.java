package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Slf4j
//@Component
@RequiredArgsConstructor
public class UserLogInOutEventListener {

    private final UserRepository userRepository;
    private final SseService sseService;
    private final UserMapper userMapper;

    private static final String EVENT_NAME_USER_UPDATED = "users.updated";

    @Async("userLogInOutExecutor")
    @EventListener
    public void onUserLogInOut(UserLogInOutEvent event) {

        UUID userId = event.userId();

        log.debug("[UserLogInOutEventListener] onUserLogInOut - userId: {}", userId);

        User user = findUser(userId);
        sseService.broadcast(EVENT_NAME_USER_UPDATED, userMapper.toDto(user));

        log.debug("[UserLogInOutEventListener] onUserLogInOut - online: {}",
            userMapper.toDto(user).online());
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException(userId));
    }
}
