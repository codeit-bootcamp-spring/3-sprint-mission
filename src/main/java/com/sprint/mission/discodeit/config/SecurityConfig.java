package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.handler.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.handler.JwtLogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Profile("dev")
    @Bean
    public CommandLineRunner logFilterChains(List<SecurityFilterChain> chains) {
        return args -> {
            int chainIdx = 1;
            for (SecurityFilterChain chain : chains) {
                var filters = chain.getFilters();
                log.info("=== SecurityFilterChain #{} ({} filters) ===", chainIdx++,
                    filters.size());
                for (int i = 0; i < filters.size(); i++) {
                    String name = filters.get(i).getClass().getSimpleName();
                    log.info("  [{} / {}] {}", i + 1, filters.size(), name);
                }
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        JwtLoginSuccessHandler jwtLoginSuccessHandler,
        JwtLogoutHandler jwtLogoutHandler,
        JwtAuthenticationFilter jwtAuthenticationFilter,
        LoginFailureHandler loginFailureHandler,
        CustomAccessDeniedHandler customAccessDeniedHandler
    ) throws Exception {
        http
            // CSRF 설정 - 인증 관련 엔드포인트는 제외
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response,
                        Supplier<CsrfToken> csrfToken) {
                        super.handle(request, response, csrfToken);
                        csrfToken.get();
                    }
                })
                .ignoringRequestMatchers("/api/sse")
                .ignoringRequestMatchers("/api/binaryContents/*/download")
            )

            // 로그인 설정
            .formLogin(login -> login
                .loginProcessingUrl("/api/auth/login")
                .successHandler(jwtLoginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()
            )

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .addLogoutHandler(jwtLogoutHandler)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .permitAll()
            )

            // 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                .requestMatchers("/api/auth/csrf-token").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers("/api/auth/refresh").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/api/binaryContents/*/download").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMIN")
                .requestMatchers("/api/sse").authenticated()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )

            // 예외 처리
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )

            // JWT 기반 STATELESS 세션 관리
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // JWT 인증 필터 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/favicon.ico", "/error")
            .requestMatchers("/static/**", "/css/**", "/js/**");
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}