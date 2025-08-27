package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.handler.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String CONFIG_NAME = "[SecurityConfig] ";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {

        return args -> {
            int filterSize = filterChain.getFilters().size();

            List<String> filters = IntStream.range(0, filterSize)
                    .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
                            filterChain.getFilters().get(idx).getClass()))
                    .toList();

            log.info(CONFIG_NAME + "현재 적용된 필터 체인 목록:");
            filters.forEach(log::info);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            LoginSuccessHandler loginSuccessHandler,
            LoginFailureHandler loginFailureHandler,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            SessionRegistry sessionRegistry,
            DiscodeitUserDetailsService discodeitUserDetailsService) throws Exception {

        log.info(CONFIG_NAME + "FilterChain 구성 시작");

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
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/auth/role").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                                .sessionRegistry(sessionRegistry)
                                .expiredUrl("/api/auth/login") // 만료된 세션 요청 시, 로그인 페이지로 이동
                        )
                )
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(new HttpSessionSecurityContextRepository()))
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("discodeit-remember-me-key")
                        .tokenValiditySeconds(60 * 60)
                        .rememberMeCookieName("remember-me")
                        .rememberMeParameter("remember-me")
                        .userDetailsService(discodeitUserDetailsService)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                        .deleteCookies("JSESSIONID", "remember-me")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
        ;

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/favicon.ico", "/error")
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/assets/**")
                .requestMatchers("/index.html");
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {

        return new SessionRegistryImpl() {

            @Override
            public void registerNewSession(String sessionId, Object principal) {
                log.info(CONFIG_NAME + "새 세션 등록 - 사용자: {}, 세션 ID: {}", principal, sessionId);
                super.registerNewSession(sessionId, principal);
                log.info(CONFIG_NAME + "현재 활성 세션 수: {}", getAllSessions(principal, false).size());
            }

            @Override
            public void removeSessionInformation(String sessionId) {
                log.info(CONFIG_NAME + "세션 제거 - 세션ID: {}", sessionId);
                super.removeSessionInformation(sessionId);
            }

            @Override
            public SessionInformation getSessionInformation(String sessionId) {
                SessionInformation info = super.getSessionInformation(sessionId);
                if (info != null) {
                    log.info(CONFIG_NAME + "세션 정보 조회 - 세션ID: {}, 만료됨: {} ", sessionId, info.isExpired());
                }
                return info;
            }
        };
    }

    @Bean
    public RoleHierarchy roleHierarchy() {

        RoleHierarchy hierarchy = RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_CHANNEL_MANAGER" +
                        "ROLE_CHANNEL_MANAGER > ROLE_USER"
                );
        log.info(CONFIG_NAME + "RoleHierarchy 설정 완료: ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");

        return hierarchy;
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy
    ) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        log.info(CONFIG_NAME + "MethodSecurityExpressionHandler 설정 완료");

        return handler;
    }
}
