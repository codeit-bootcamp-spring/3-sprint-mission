package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.translator.SecurityExceptionTranslator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForbiddenAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;
  private final SecurityExceptionTranslator translator;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    DiscodeitException discodeitException = translator.translate(accessDeniedException);
    ErrorResponse errorResponse = ErrorResponse.from(discodeitException);

    response.setStatus(discodeitException.getErrorCode().getStatus());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
