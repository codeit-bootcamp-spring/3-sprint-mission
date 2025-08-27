package com.sprint.mission.discodeit.auth.util;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OnlineStatusUtil {

    private final SessionRegistry sessionRegistry;

    // 유저가 현재 세션 레지스트리에 등록되어 있는지 확인
    // 이름 수정하는 상황에 대비해 id로 온라인 여부 확인하도록 수정
    public boolean isOnlineUser(UUID userId) {
        try {
            // SessionRegistry에서 모든 주체(Principal) 조회
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

            // 해당 사용자의 모든 세션 정보 찾기
            for (Object principal : allPrincipals) {
                if (principal instanceof DiscodeitUserDetails userDetails) {
                    UUID principalId = userDetails.getId();
                    log.info("[OnlineStatusUtil] 온라인 여부 확인 중: {}", principalId);

                    if (userId.equals(principalId)) {
                        // 만료되지 않은 세션만 체크
                        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                        return !sessions.isEmpty();
                    }
                }
            }
            return false;

        } catch (Exception e) {
            log.error("[OnlineStatusUtil] 온라인 여부 체크 중 오류 발생: {}", e.getMessage());
            log.error("[OnlineStatusUtil] 오류 설명 메시지", e);
            // 오류 발생 시 false 반환
            return false;
        }
    }
}
