package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.handler.CustomSessionExpiredStrategy;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private static final int REMEMBER_ME_VALIDITY_SECONDS = 60 * 60 * 24 * 7;

    @Profile("dev")
    @Bean
    public CommandLineRunner logFilterChains(List<SecurityFilterChain> chains) {
        return args -> {
            int chainIdx = 1;
            for (SecurityFilterChain chain : chains) {
                var filters = chain.getFilters();
                log.info("=== SecurityFilterChain #{} ({} filters) ===", chainIdx++, filters.size());
                for (int i = 0; i < filters.size(); i++) {
                    String name = filters.get(i).getClass().getSimpleName();
                    log.info("  [{} / {}] {}", i + 1, filters.size(), name);
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        LoginSuccessHandler loginSuccessHandler,
        LoginFailureHandler loginFailureHandler,
        SessionRegistry sessionRegistry,
        CustomAccessDeniedHandler customAccessDeniedHandler,
        UserDetailsService userDetailsService,
        JdbcTokenRepositoryImpl jdbcTokenRepository
    ) throws Exception {
        http
            // CSRF 설정 - 쿠키 기반 CSRF 토큰 사용
            .csrf(csrf -> csrf
                // 쿠키 기반 CSRF 토큰 사용
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // CSRF 토큰 요청 처리 핸들러 설정
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )

            // 로그인 설정
            .formLogin(login -> login
                .loginProcessingUrl("/api/auth/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()
            )

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .permitAll()
            )

            // 예외 처리 (401 / 403 응답)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler(customAccessDeniedHandler)
            )

            // 동시 로그인 제어
            .sessionManagement(management -> management
                .sessionConcurrency(concurrency -> concurrency
                    .maximumSessions(1)                 // 한 사용자당 최대 1세션
                    .maxSessionsPreventsLogin(false)        // 새 로그인 허용
                    .expiredSessionStrategy(new CustomSessionExpiredStrategy()) // 기존 세션 만료 시 JSON 응답
                    .sessionRegistry(sessionRegistry)   // 세션 추적
                )
            )

            // 세션 컨텍스트 저장소 명시적 설정
            .securityContext(securityContext -> securityContext
                .securityContextRepository(new HttpSessionSecurityContextRepository())
            )

            // 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 메인 페이지 및 개발 도구는 인증 불필요
                .requestMatchers("/").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()

                // 인증 없이 접근 가능한 API
                .requestMatchers("/api/auth/csrf-token").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()

                // 나머지 API는 인증 필요
                .requestMatchers("/api/**").authenticated()

                // 그 외 모든 요청은 허용
                .anyRequest().permitAll()
            )

            .rememberMe(rememberMe -> rememberMe
                .rememberMeServices(rememberMeServices(userDetailsService, jdbcTokenRepository))
                .key("a-very-security-key")
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService)
                .tokenValiditySeconds(REMEMBER_ME_VALIDITY_SECONDS)
            );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            // 브라우저 기본 요청 및 에러 페이지
            .requestMatchers("/favicon.ico", "/error")
            .requestMatchers("/static/**", "/css/**", "/js/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl() {
            @Override
            public void registerNewSession(String sessionId, Object principal) {
                log.info("[SessionRegistry] 새 세션 등록 - 사용자={}, 세션ID={}", principal, sessionId);
                super.registerNewSession(sessionId, principal);
            }

            @Override
            public void removeSessionInformation(String sessionId) {
                log.info("[SessionRegistry] 세션 제거 - 세션ID={}", sessionId);
                super.removeSessionInformation(sessionId);
            }

            @Override
            public SessionInformation getSessionInformation(String sessionId) {
                SessionInformation info = super.getSessionInformation(sessionId);
                if (info != null) {
                    log.debug("[SessionRegistry] 세션 조회 - 세션ID={}, 만료됨={}", sessionId,
                        info.isExpired());
                }
                return info;
            }
        };
    }

    // 세션 종료시 세션 정보를 자동 정리하는 이벤트
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    // ---------------- db기반 자동로그인 --------------------
    // token 저장소
    @Bean
    public JdbcTokenRepositoryImpl jdbcTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    // db 기반 rememberMeService
    @Bean
    public PersistentTokenBasedRememberMeServices rememberMeServices(
        UserDetailsService userDetailsService,
        JdbcTokenRepositoryImpl jdbcTokenRepository
    ) {
        PersistentTokenBasedRememberMeServices services =
            new PersistentTokenBasedRememberMeServices(
                "a-very-security-key", userDetailsService, jdbcTokenRepository);

        services.setTokenValiditySeconds(REMEMBER_ME_VALIDITY_SECONDS); // 7일 유지
        services.setCookieName("remember-me");
        services.setParameter("remember-me");

        return services;
    }
}