package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.event.payload.UserLoginEvent;
import com.sprint.mission.discodeit.event.payload.UserLogoutEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserCacheEvictionListener {

    @EventListener
    @CacheEvict(value = "users", allEntries = true)
    public void onUserLogin(UserLoginEvent event) {
        log.info("[캐시 무효화] 사용자 로그인 → 사용자 목록(users) 캐시 삭제, userId={}", event.userId());
    }

    @EventListener
    @CacheEvict(value = "users", allEntries = true)
    public void onUserLogout(UserLogoutEvent event) {
        log.info("[캐시 무효화] 로그아웃 → 사용자 목록(users) 캐시 삭제, userId={}", event.userId());
    }
}