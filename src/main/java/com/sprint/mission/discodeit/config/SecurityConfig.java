package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Spring Security 설정을 담당하는 설정 클래스입니다.
 * 
 * <p>JWT 기반 인증, 권한 관리, CSRF 보호, 세션 관리 등을 설정합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>JWT 기반 무상태 인증 설정</li>
 *   <li>권한 기반 접근 제어</li>
 *   <li>CSRF 토큰 보호</li>
 *   <li>로그인/로그아웃 핸들러 설정</li>
 *   <li>메소드 보안 활성화</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String CONFIG_NAME = "[SecurityConfig] ";

    /**
     * 비밀번호 인코딩을 위한 BCryptPasswordEncoder를 제공합니다.
     * 
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 애플리케이션 시작 시 현재 적용된 보안 필터 체인을 로깅합니다.
     * 
     * @param filterChain 현재 적용된 보안 필터 체인
     * @return CommandLineRunner 인스턴스
     */
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

    /**
     * HTTP 보안 설정을 구성합니다.
     * 
     * <p>CSRF 보호, 권한 관리, 세션 정책, 로그인/로그아웃 설정을 포함합니다.</p>
     * 
     * @param http HttpSecurity 인스턴스
     * @param loginSuccessHandler 로그인 성공 핸들러
     * @param loginFailureHandler 로그인 실패 핸들러
     * @param accessDeniedHandler 접근 거부 핸들러
     * @param authenticationEntryPoint 인증 진입점 핸들러
     * @param jwtLogoutHandler JWT 로그아웃 핸들러
     * @return 구성된 SecurityFilterChain
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtLoginSuccessHandler loginSuccessHandler,
            LoginFailureHandler loginFailureHandler,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            JwtLogoutHandler jwtLogoutHandler,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        log.info(CONFIG_NAME + "FilterChain 구성 시작");

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                        // SockJs/WebSocket 핸드셰이크 경로 CSRF 예외
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/ws/**"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers("/api/auth/csrf-token").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/api/notifications/**").permitAll()
                        .requestMatchers("/api/auth/role").hasRole("ADMIN")

                        // 웹소켓 핸드셰이크/정보/폴백 경로 허용
                        .requestMatchers("/ws/**").permitAll()

                        // SSE
                        .requestMatchers("/api/sse/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
                        )
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    /**
     * 웹 보안 설정을 커스터마이징합니다.
     * 
     * <p>정적 리소스와 에러 페이지에 대한 보안 검사를 무시합니다.</p>
     * 
     * @return WebSecurityCustomizer 인스턴스
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/favicon.ico", "/error")
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/assets/**")
                .requestMatchers("/index.html");
    }
    
    /**
     * 역할 계층 구조를 정의합니다.
     * 
     * <p>ADMIN > CHANNEL_MANAGER > USER 순서로 권한이 상속됩니다.</p>
     * 
     * @return 구성된 RoleHierarchy 인스턴스
     */
    @Bean
    public RoleHierarchy roleHierarchy() {

        RoleHierarchy hierarchy = RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n" +
                        "ROLE_CHANNEL_MANAGER > ROLE_USER"
                );
        log.info(CONFIG_NAME + "RoleHierarchy 설정 완료: ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");

        return hierarchy;
    }

    /**
     * 메소드 보안 표현식 핸들러를 구성합니다.
     * 
     * <p>역할 계층 구조를 메소드 보안에 적용합니다.</p>
     * 
     * @param roleHierarchy 역할 계층 구조
     * @return 구성된 MethodSecurityExpressionHandler 인스턴스
     */
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
