package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.ErrorCode;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException ex
    ) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            errorCode.getCode(),
            errorCode.getMessage(),
            Map.of(
                "path", request.getRequestURI(),
                "method", request.getMethod()
            ),
            ex.getClass().getSimpleName(),
            errorCode.getStatus().value()
        );

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
