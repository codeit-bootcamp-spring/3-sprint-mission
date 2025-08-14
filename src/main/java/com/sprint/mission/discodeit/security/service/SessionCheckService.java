package com.sprint.mission.discodeit.security.service;

import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

/**
 * SessionRegistry를 이용해 사용자의 로그인 여부와 활성 세션 정보를 조회하는 유틸 서비스.
 */
@Service
@RequiredArgsConstructor
public class SessionCheckService {

	private final SessionRegistry sessionRegistry;

	/**
	 * (권장) 사용자 PK(UUID)로 로그인 여부 확인
	 *
	 * @return true 면 "현재 로그인 중"
	 */
	public boolean isUserLoggedIn(UUID userId) {
		// 1) 레지스트리에 올라와 있는 모든 principal 순회
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			// 2) 우리 프로젝트의 Principal 타입으로만 검사
			if (principal instanceof DiscodeitUserDetails userDetails) {
				if (userId.equals(userDetails.getUserDto().id())) {
					// 3) 만료되지 않은(active) 세션만 가져옴
					List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails,
						false);
					// 4) 1개 이상 있으면 로그인 중
					return !sessions.isEmpty();
				}
			}
		}
		return false;
	}

	// (리스트 최적화용) 현재 로그인 중인 모든 사용자 ID 수집
	public Set<UUID> getOnlineUserIds() {
		Set<UUID> online = new HashSet<>();
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			if (principal instanceof DiscodeitUserDetails dud) {
				if (!sessionRegistry.getAllSessions(dud, false).isEmpty()) {
					online.add(dud.getUserDto().id());
				}
			}
		}
		return online;
	}
}