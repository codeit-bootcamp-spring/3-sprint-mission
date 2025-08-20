// CSRF 발급 엔드포인트: GET /api/auth/csrf-token
// - Spring Security가 생성한 CsrfToken을 파라미터로 바로 주입받음
// - 쿠키는 CookieCsrfTokenRepository가 알아서 내려줌(이미 설정됨)
// - JSON으로도 토큰/헤더명을 함께 반환(프론트에서 디버깅/확인용)
package com.sprint.mission.discodeit.security.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

	@GetMapping("/api/auth/csrf-token")
	public ResponseEntity<Map<String, String>> csrf(CsrfToken token) {
		// token은 Null 아님: CsrfFilter가 앞에서 생성/주입
		return ResponseEntity.ok(Map.of(
			"headerName", token.getHeaderName(),   // 보통 "X-XSRF-TOKEN"
			"parameterName", token.getParameterName(), // 보통 "_csrf"
			"token", token.getToken()              // 실제 토큰 값 (쿠키에도 XSRF-TOKEN으로 세팅됨)
		));
	}
}
