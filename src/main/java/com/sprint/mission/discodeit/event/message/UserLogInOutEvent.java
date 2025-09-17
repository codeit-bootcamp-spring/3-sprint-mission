package com.sprint.mission.discodeit.event.message;

import java.util.UUID;

/**
 * 사용자 로그인/로그아웃 상태 변경을 위한 이벤트
 * 분산 환경에서 SSE를 통해 다른 클라이언트들에게 사용자 상태 변경을 알리기 위해 사용
 * */
public record UserLogInOutEvent(
    UUID userId,
    boolean isLoggedIn
) {

    /**
     * 로그인 이벤트 생성
     * */
    public static UserLogInOutEvent logIn(UUID userId) {
        return new UserLogInOutEvent(userId, true);
    }

    /**
     * 로그아웃 이벤트 생성
     * */
    public static UserLogInOutEvent logOut(UUID userId) {
        return new UserLogInOutEvent(userId, false);
    }
}
