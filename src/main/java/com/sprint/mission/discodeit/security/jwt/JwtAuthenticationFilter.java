package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService discodeitUserDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtRegistry jwtRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String token = resolverBearer(request);

                if (StringUtils.hasText(token) && jwtTokenProvider.validateAccessToken(token)) {
                    if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                        sendUnauthorized(response, "만료되었거나 무효화된 토큰입니다.");
                        return;
                    }

                    String username = jwtTokenProvider.getUsername(token);
                    if (StringUtils.hasText(username)) {
                        UserDetails user = discodeitUserDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "인증 처리 중 오류가 발생했습니다.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String resolverBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return null;
        }

        return header.substring(7);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.createObjectNode()
                        .put("success", false)
                        .put("message", message)
                        .toString()
        );
    }

    private void send401(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.createObjectNode()
                        .put("success", false)
                        .put("message", message)
                        .toString()
        );
    }
}