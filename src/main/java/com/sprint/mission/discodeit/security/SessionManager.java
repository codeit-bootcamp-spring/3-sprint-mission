package com.sprint.mission.discodeit.security;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

/**
 * 사용자 세션을 관리하는 클래스.
 *
 * <p>
 * Spring Security의 {@link SessionRegistry}를 활용하여 특정 사용자 ID의 세션을 조회,
 * 무효화 및 활성 여부 확인 기능을 제공한다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

    private final SessionRegistry sessionRegistry;

    /**
     * 특정 사용자 ID에 대한 활성 세션 목록을 조회한다.
     *
     * <p>
     * {@link DiscodeitUserDetails} 타입의 Principal만 조회 대상으로 간주한다.
     * </p>
     *
     * @param userId 사용자 UUID
     * @return 활성 세션 정보 리스트 (없을 경우 빈 리스트 반환)
     */
    public List<SessionInformation> getActiveSessionsByUserId(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
            // Principal 객체 중 DiscodeitUserDetails만 필터링
            .filter(principal -> principal instanceof DiscodeitUserDetails)
            .map(DiscodeitUserDetails.class::cast)
            .filter(details -> details.getUserDto().id().equals(userId))
            .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
            .toList();
    }

    /**
     * 특정 사용자 ID에 대한 모든 활성 세션을 무효화한다.
     *
     * <p>
     * 세션이 존재하는 경우 즉시 만료 처리하며, 무효화된 세션 수를 로그에 남긴다.
     * </p>
     *
     * @param userId 사용자 UUID
     */
    public void invalidateSessionsByUserId(UUID userId) {
        List<SessionInformation> activeSessionInfos = getActiveSessionsByUserId(userId);

        if (!activeSessionInfos.isEmpty()) {
            // 각 세션 즉시 만료 처리
            activeSessionInfos.forEach(SessionInformation::expireNow);
            log.debug("{}개의 세션이 무효화되었습니다.", activeSessionInfos.size());
        }
    }

    /**
     * 특정 사용자 ID에 대해 활성 세션이 존재하는지 확인한다.
     *
     * @param userId 사용자 UUID
     * @return 활성 세션이 하나라도 존재하면 true, 없으면 false
     */
    public boolean hasActiveSessions(UUID userId) {
        return !getActiveSessionsByUserId(userId).isEmpty();
    }

}
