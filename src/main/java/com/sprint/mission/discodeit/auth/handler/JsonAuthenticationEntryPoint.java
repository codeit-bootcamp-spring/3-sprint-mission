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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException ex
    ) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_REQUIRED;

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