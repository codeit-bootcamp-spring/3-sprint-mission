package com.sprint.mission.discodeit.event.message;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLogInOutEvent {
    private final UUID userId;
    private final boolean loggedIn; // true = 로그인, false = 로그아웃
}