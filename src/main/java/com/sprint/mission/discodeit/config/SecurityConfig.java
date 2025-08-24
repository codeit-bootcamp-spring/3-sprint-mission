package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.Http403ForbiddenAccessDeniedHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

/**
 * 최소 구성의 Spring Security 설정 클래스.
 *
 * <p>주요 특징:</p>
 * <ul>
 *   <li>CSRF 방어 활성화 및 SPA 환경 대응</li>
 *   <li>폼 로그인 처리 및 로그인 성공/실패 핸들러 설정</li>
 *   <li>로그아웃 URL 및 성공 처리 핸들러 설정</li>
 *   <li>Remember-Me 기능 기본 활성화</li>
 *   <li>Role 계층 구조 정의 및 Method Security 지원</li>
 *   <li>동시 세션 제어 및 SessionRegistry/HttpSessionEventPublisher 제공</li>
 *   <li>비밀번호 인코딩을 위한 {@link BCryptPasswordEncoder} 제공</li>
 *   <li>애플리케이션 시작 시 SecurityFilterChain 디버그 로그 출력</li>
 * </ul>
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * HTTP 보안 필터 체인 구성.
     *
     * <ul>
     *   <li>CSRF 토큰을 쿠키에 저장(HttpOnly=false), SPA 헤더 기반 요청 대응</li>
     *   <li>폼 로그인 처리: 성공/실패 시 커스텀 핸들러 적용</li>
     *   <li>로그아웃 처리: 지정 URL, 성공 시 204 반환</li>
     *   <li>Remember-Me 기능 기본 활성화</li>
     *   <li>특정 요청 제외하고 인증 필요</li>
     *   <li>권한 부족 시 403 JSON 응답 처리</li>
     *   <li>동시 세션 최대 1개 제한, SessionRegistry 사용</li>
     * </ul>
     *
     * @param http HttpSecurity 객체
     * @param loginSuccessHandler 로그인 성공 시 호출될 핸들러
     * @param loginFailureHandler 로그인 실패 시 호출될 핸들러
     * @param objectMapper JSON 변환용 ObjectMapper
     * @param sessionRegistry 동시 세션 제어용 SessionRegistry
     * @return SecurityFilterChain
     * @throws Exception 보안 구성 중 발생 가능한 예외
     */
    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        LoginSuccessHandler loginSuccessHandler,
        LoginFailureHandler loginFailureHandler,
        ObjectMapper objectMapper,
        SessionRegistry sessionRegistry
    )
        throws Exception {
        http
            // CSRF 보호 설정
            .csrf(csrf -> csrf
                // 토큰을 HttpOnly=false 옵션으로 쿠키에 저장
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // SPA 대응
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )
            // 폼 로그인 처리 과정
            .formLogin(login -> login
                // 클라이언트가 로그인 요청을 보낼 URL 지정
                .loginProcessingUrl("/api/auth/login")
                // 로그인 성공 시 호출될 커스텀 핸들러
                .successHandler(loginSuccessHandler)
                // 로그인 실패 시 호출될 커스텀 핸들러
                .failureHandler(loginFailureHandler)
            )
            // 로그아웃 처리 설정
            .logout(logout -> logout
                // 클라이언트가 로그아웃 요청을 보낼 URL 지정
                .logoutUrl("/api/auth/logout")
                // 로그아웃 성공 시 204 No Content 반환
                .logoutSuccessHandler(
                    new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            )
            // 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/auth/csrf-token"),
                    AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/users"),
                    AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/login"),
                    AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/logout"),
                    new NegatedRequestMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
                ).permitAll()
                .anyRequest().authenticated()
            )
            // 예외 처리
            .exceptionHandling(ex -> ex
                // 인증 실패
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                // 권한 부족
                .accessDeniedHandler(new Http403ForbiddenAccessDeniedHandler(objectMapper))
            )
            // 동시 세션 제어
            .sessionManagement(session -> session
                .sessionConcurrency(concurrency -> concurrency
                    .maximumSessions(1)
                    .sessionRegistry(sessionRegistry)
                )
            )
            // Remember-Me 기능 활성화
            .rememberMe(Customizer.withDefaults());

        // 현재 HttpSecurity 상태를 기반으로 SecurityFilterChain 생성
        return http.build();
    }

    /**
     * 애플리케이션 시작 시 SecurityFilterChain에 등록된 필터 목록을 출력.
     *
     * @param filterChain SecurityFilterChain
     * @return CommandLineRunner 실행 객체
     */
    @Bean
    public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {
        return args -> {
            int filterSize = filterChain.getFilters().size();
            // 각 필터의 순서와 클래스 이름 출력
            List<String> filterNames = IntStream.range(0, filterSize)
                .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
                    filterChain.getFilters().get(idx).getClass()))
                .toList();
            log.debug("Filter Chain Debug...\n{}",
                String.join(System.lineSeparator(), filterNames));
        };
    }

    /**
     * 비밀번호 인코딩을 위한 PasswordEncoder Bean.
     *
     * <p>BCrypt 해시 알고리즘을 사용하여 안전하게 비밀번호를 저장/검증한다.</p>
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt : 내부적으로 salt를 포핳마여 매번 다른 해시 결과 생성
        return new BCryptPasswordEncoder();
    }

    /**
     * Role 계층 구조 정의.
     * <p>ADMIN > USER, CHANNEL_MANAGER / CHANNEL_MANAGER > USER</p>
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role(Role.ADMIN.name())
            .implies(Role.USER.name(), Role.CHANNEL_MANAGER.name())

            .role(Role.CHANNEL_MANAGER.name())
            .implies(Role.USER.name())

            .build();
    }

    /**
     * Method Security 표현식 처리기.
     * <p>RoleHierarchy를 적용하여 @PreAuthorize, @PostAuthorize 등에서 역할 계층 사용 가능</p>
     */
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    // 동시 세션 제어를 위한 SessionRegistry Bean
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // HttpSession 이벤트를 감지하여 동시 세션 관리에 반영
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
