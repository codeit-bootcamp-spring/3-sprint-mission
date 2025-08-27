package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.auth.handler.CustomSecuritySessionExpiredStrategy;
import com.sprint.mission.discodeit.auth.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.auth.handler.LoginSuccessHandler;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Spring Security 설정
 */
@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    /*
     * 필터 체인 디버깅을 위한 Bean 설정
     * CommandLineRunner를 사용해 애플리케이션 시작 시 필터 체인의 클래스 이름을 출력하여 디버깅 용도로 사용한다.
     */
    @Bean
    public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {

        return args -> {
            int filterSize = filterChain.getFilters().size();

            List<String> filterNames = IntStream.range(0, filterSize)
                    .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
                            filterChain.getFilters().get(idx).getClass()))
                    .toList();

            System.out.println("현재 적용된 필터 체인 목록:");
            filterNames.forEach(System.out::println);
        };
    }


     // 사용자의 비밀번호를 BCrypt 암호화하기 위한 Bean 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http
                                , SessionRegistry sessionRegistry
                                , LoginSuccessHandler loginSuccessHandler
                                , LoginFailureHandler loginFailureHandler
                                , CustomAccessDeniedHandler customAccessDeniedHandler
    ) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                .requestMatchers("/api/auth/csrf-token").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()

                // 퍼블릭 채널 생성, 수정, 삭제는 CHANNEM_MANAGER 권한을 가져야 함
                .requestMatchers(HttpMethod.POST, "/api/channels/public").hasRole("CHANNEL_MANAGER")
                .requestMatchers(HttpMethod.PATCH, "/api/channels/**").hasRole("CHANNEL_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/channels/**").hasRole("CHANNEL_MANAGER")

                // 사용자 권한 수정은 ADMIN 권한을 가져야 함
                .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            // 세션 관리 설정
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .sessionConcurrency(concurrency -> concurrency
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                    .sessionRegistry(sessionRegistry())
                    .expiredSessionStrategy(new CustomSecuritySessionExpiredStrategy())
                )
            )

            // Form 기반 로그인 활성화
            .formLogin(formLogin -> formLogin
                // 로그인 처리 URL
                .loginProcessingUrl("/api/auth/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
            )

            // 로그아웃 설정
            .logout(logout -> logout
                // 로그아웃 처리 URL
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(
                    new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
                )
            )

            // remember-me 설정
            .rememberMe(remember -> remember
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(3600)
                .alwaysRemember(false)
            )

            // 권한 실패 예외 처리 설정
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler)
            )
            ;
        return http.build();
    }

    // WebSecurity 설정 - 정적 리소스는 Spring Security 필터 체인에서 완전히 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            // 브라우저 기본 요청 및 에러 페이지
            .requestMatchers("/favicon.ico", "/error")
            // 정적 리소스 (CSS, JavaScript, 이미지 등)
            .requestMatchers("/index.html", "/static/**", "/assets/**", "/images/**");
    }

    // SessionRegistry Bean 설정
    @Bean
    public SessionRegistry sessionRegistry() {
        // 세션 레지스트리 구현체를 상속받아 커스터마이징
        return new SessionRegistryImpl() {

            @Override
            public void registerNewSession(String sessionId, Object principal) {
                log.info("[SecurityConfig] 새로운 세션 등록 - 사용자 : {}", principal);
                super.registerNewSession(sessionId, principal);
            }

            @Override
            public SessionInformation getSessionInformation(String sessionId) {
                log.info("[SecurityConfig] 세션 정보 조회 - 세션 id: {}", sessionId);
                return super.getSessionInformation(sessionId);
            }

            @Override
            public void removeSessionInformation(String sessionId) {
                log.info("[SecurityConfig] 세션 제거 - 세션 id: {}", sessionId);
                super.removeSessionInformation(sessionId);
            }
        };
    }

    // RoleHierarchy Bean 설정
    @Bean
    public RoleHierarchy roleHierarchy() {

        RoleHierarchy hierarchy = RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");
        log.info("[SecurityConfig] RoleHierarchy 설정 완료: ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");

        return hierarchy;
    }

    // Method Security에서 RoleHierarchy를 사용하기 위한 설정
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(

        RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        log.info("[SecurityConfig] MethodSecurityExpressionHandler 설정 완료");
        return handler;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
