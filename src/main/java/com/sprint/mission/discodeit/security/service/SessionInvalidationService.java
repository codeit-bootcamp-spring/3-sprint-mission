package com.sprint.mission.discodeit.security.service;

import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionInvalidationService {

	private final SessionRegistry sessionRegistry;

	/**
	 * 주어진 사용자 ID의 "모든 활성 세션"을 만료(expire) 처리한다.
	 *
	 * @param userId 권한이 변경된 사용자 PK(UUID)
	 * @return 만료 처리된 세션 개수
	 */
	public int expireUserSessionsByUserId(UUID userId) {
		int expiredCount = 0;

		// 현재 레지스트리에 올라와있는 모든 principal 순회
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			if (principal instanceof DiscodeitUserDetails userDetails) {
				// DiscodeitUserDetails#equals/hashCode가 PK 기반으로 되어 있어야 함 (이미 보강 완료)
				UUID principalId = userDetails.getUserDto().id();
				if (userId != null && userId.equals(principalId)) {

					// 해당 principal의 모든 세션 조회(false = 만료된 세션 포함 X)
					List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails,
						false);
					for (SessionInformation si : sessions) {
						// 즉시 만료 표시 → 다음 요청에서 ConcurrentSessionFilter가 감지하여 세션 파기
						si.expireNow();
						log.info("[SessionInvalidation] userId={} sessionId={} expired", userId,
							si.getSessionId());
						expiredCount++;
					}
				}
			}
		}
		log.info("[SessionInvalidation] userId={} totalExpired={}", userId, expiredCount);
		return expiredCount;
	}
}
