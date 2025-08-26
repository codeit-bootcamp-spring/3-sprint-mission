package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.handler.Http403ForbiddenAccessDeniedHandler;
import com.sprint.mission.discodeit.handler.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.handler.JwtLogoutHandler;
import com.sprint.mission.discodeit.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.jwt.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.basic.DiscodeitUserDetailsService;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public static HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtRegistry jwtRegistry(JwtTokenProvider jwtTokenProvider) {
        return new InMemoryJwtRegistry(1, jwtTokenProvider);
    }

    @Bean
    public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {
        return args -> {
            int filterSize = filterChain.getFilters().size();

            List<String> filterNames = IntStream.range(0, filterSize)
                    .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
                            filterChain.getFilters().get(idx).getClass()))
                    .toList();

            /* System.outprintln()은 사용하지 않는 것이 좋다.
             * 이유: 1. 블로킹 I/O로 인한 성능 저하
             * 2. 동기화로 인한 병목 (특히 멀티스레드 환경)
             * 3. 불필요한 문자열 연산 (메모리, GC 측면에서도 비효율적임)
             * 4. 로그 관리 안됨 (특히 운영 환경에서 적합하지 않음)
             */
            log.debug("필터 체인 목록... \n{}", String.join("\n", filterNames) + "\n");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter,
                                           JwtLoginSuccessHandler jwtLoginSuccessHandler,
                                           JwtLogoutHandler jwtLogoutHandler,
                                           LoginFailureHandler loginFailureHandler,
                                           JwtLogoutHandler logoutSuccessHandler,
                                           SessionRegistry sessionRegistry,
                                           DiscodeitUserDetailsService userDetailsService,
                                           HttpSessionEventPublisher httpSessionEventPublisher,
                                           ObjectMapper objectMapper
    ) throws Exception {

        http
                // csrf 설정
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Form 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(jwtLoginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .permitAll()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(
                                        HttpStatus.NO_CONTENT)
                        )
                )

                // 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 불필요
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/auth/csrf-token"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/users"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/login"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/refresh"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/logout"),
                                new NegatedRequestMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 세션 관리 설정
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Rememeber-Me 설정
                .rememberMe(remember -> remember
                        .key("hello-its-me-remember-me")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60 * 60 * 24 * 7)
                        .userDetailsService(userDetailsService)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                        .accessDeniedHandler(new Http403ForbiddenAccessDeniedHandler(objectMapper)));

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {

        return RoleHierarchyImpl
                .withDefaultRolePrefix()
                .role(Role.ADMIN.name())
                .implies(Role.USER.name(), Role.CHANNEL_MANAGER.name())

                .role(Role.CHANNEL_MANAGER.name())
                .implies(Role.USER.name())

                .build();
    }
}