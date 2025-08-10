package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForbiddenAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    ErrorResponse errorResponse = new ErrorResponse(
        "FORBIDDEN",
        "접근 권한이 없습니다.",
        accessDeniedException.getClass().getSimpleName(),
        HttpServletResponse.SC_FORBIDDEN,
        Instant.now(),
        Map.of()
    );

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
