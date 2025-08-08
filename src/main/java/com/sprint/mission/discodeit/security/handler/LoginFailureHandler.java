package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception)
		throws IOException, ServletException {

		log.warn("로그인 실패: {}", exception.getMessage());

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		// 요구사항: 401 ErrorResponse 로 응답
		objectMapper.writeValue(response.getWriter(),
			new ErrorResponse("AUTHENTICATION_FAILED", exception.getMessage()));
	}

	@Value
	@JsonInclude(JsonInclude.Include.NON_NULL)
	static class ErrorResponse {

		String error;   // 에러 코드
		String message; // 설명
	}

}
