package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.security.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.CustomSessionExpiredStrategy;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
        SessionRegistry sessionRegistry) throws Exception {
        http
            .formLogin(login -> login
                .loginProcessingUrl("/api/auth/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(
                    new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)) // 204
                .permitAll()
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/favicon.ico",
                    "/index-*.js",
                    "/index-*.css",
                    "/assets/**",

                    "/api/auth/login",
                    "/api/auth/logout",
                    "/api/auth/csrf-token",
                    "/api/users",

                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler)
            )

            .sessionManagement(session -> session
                // 세션 고정 공격 방지를 위해 세션 마이그레이션 설정(새 세션을 생성하고 기존 세션의 모든 속성을 복사)
                .sessionFixation().migrateSession()
                // 동시 로그인 제한(하나의 계정당 하나의 세션만 허용)
                .maximumSessions(1)
                // 새 로그인 시 기존 세션 무효화(false: 기존 세션 무효화, true: 기존 세션 무효화 안함)
                .maxSessionsPreventsLogin(false)
                // 세션 레지스트리 (동시 세션 제어시 필수)
                .sessionRegistry(sessionRegistry)
                // 세션 만료 처리 전략(커스터마이징한 예외 처리 핸들러를 사용)
                .expiredSessionStrategy(new CustomSessionExpiredStrategy())
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "UNAUTHORIZED",
                "로그인이 필요합니다.",
                Map.of("path", request.getRequestURI()),
                authException.getClass().getSimpleName(),
                HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                new ObjectMapper().writeValueAsString(errorResponse)
            );
        };
    }

    @Bean
    public RoleHierarchy roleHierarchy() {

        RoleHierarchy hierarchy = RoleHierarchyImpl.fromHierarchy(
            "ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");
        return hierarchy;
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }


    @Bean
    public SessionRegistry sessionRegistry() {

        // 세션 레지스트리 구현체를 상속받아 커스터마이징
        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl() {

            // 새 세션 등록 시 추가 로깅
            @Override
            public void registerNewSession(String sessionId, Object principal) {
                System.out.println(
                    "[SessionRegistry] 새 세션 등록 - 사용자: " + principal + ", 세션ID: " + sessionId);
                super.registerNewSession(sessionId, principal);
                System.out.println(
                    "[SessionRegistry] 현재 활성 세션 수: " + getAllSessions(principal, false).size());
            }

            // 세션 제거 시 추가 로깅
            @Override
            public void removeSessionInformation(String sessionId) {
                System.out.println("[SessionRegistry] 세션 제거 - 세션ID: " + sessionId);
                super.removeSessionInformation(sessionId);
            }

            // 세션 정보 조회 시 추가 로깅
            @Override
            public SessionInformation getSessionInformation(String sessionId) {
                SessionInformation info = super.getSessionInformation(sessionId);
                if (info != null) {
                    System.out.println("[SessionRegistry] 세션 정보 조회 - 세션ID: " + sessionId + ", 만료됨: "
                        + info.isExpired());
                }
                return info;
            }
        };

        return sessionRegistry;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }
}

