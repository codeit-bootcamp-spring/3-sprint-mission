package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.auth.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.auth.handler.LoginSuccessHandler;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers("/favicon.ico", "/error", "/assets/**", "/static/**", "/index.html",
                "/user-list.html", "/script.js", "/styles.css");
    }

    @Bean
    public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {
        return args -> {
            int filterSize = filterChain.getFilters().size();

            List<String> filterNames = IntStream.range(0, filterSize).mapToObj(
                idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
                    filterChain.getFilters().get(idx).getClass())).toList();

            System.out.println("현재 적용된 필터 체인 목록:");
            filterNames.forEach(System.out::println);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
        LoginSuccessHandler loginSuccessHandler,
        LoginFailureHandler loginFailureHandler,
        CustomAccessDeniedHandler customAccessDeniedHandler)
        throws Exception {
        log.debug("[SecurityConfig] FilterChain 구성 시작");

        http
            // CSRF 설정 - 쿠키 기반 CSRF 토큰 사용
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // CSRF 토큰 요청 처리 핸들러 설정
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers("/h2-console/**")) // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                // API가 아닌 요청
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/", "/favicon.ico", "/error", "/assets/**", "/static/**")
                .permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                // 인증 없이 접근 가능한 API
                .requestMatchers("/api/auth/csrf-token").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()     // 회원가입
                .requestMatchers(("/api/auth/login")).permitAll() // 로그인
                .requestMatchers(("/api/auth/logout")).permitAll() // 로그아웃
                .requestMatchers(("/api/auth/me")).permitAll()

                // 퍼블릭 채널 생성, 수정, 삭제는 CHANNEL_MANAGER 권한을 가져야함
                .requestMatchers(HttpMethod.POST, "/api/channels/public").hasRole("CHANNEL_MANAGER")
                .requestMatchers(HttpMethod.PATCH, "/api/channels/").hasRole("CHANNEL_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/channels/").hasRole("CHANNEL_MANAGER")

                // 사용자 권한 수정은 ADMIN 권한을 가져야함
                .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMIN")

                .anyRequest().authenticated())

            // 세션 관리 설정
            .sessionManagement(session -> session
                //세션 고정 공격 방지를 위해 세션 마이그레이션 설정(새 세션을 생성하고 기존 세션의 모든 속성을 복사)
                .sessionFixation().migrateSession()
                // 동시 로그인 제한(하나의 계정 당 세션 1개만 허용
                .maximumSessions(1)
                // 새 로그인 시 기존 세션 무효화(false: 기존 세션 무효화, true: 무효화 안함)
                .maxSessionsPreventsLogin(false)
            )
            .headers(headers -> headers
                .frameOptions(FrameOptionsConfig::sameOrigin))

            // Form 기반 로그인 활성화
            .formLogin(login -> login
                .loginProcessingUrl("/api/auth/login")
                .successHandler(loginSuccessHandler) // 로그인 성공 핸들러
                .failureHandler(loginFailureHandler) // 로그인 실패 핸들러
                .permitAll())
            .httpBasic(AbstractHttpConfigurer::disable)
            // 로그 아웃 설정
            .logout(
                logout -> logout
                    .logoutUrl("/api/auth/logout").logoutSuccessHandler(
                        new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                    .permitAll())
            // 예외 처리 (적절한 권한이 없는 경우)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler));

        log.debug("[SecurityConfig] FilterChain 구성 완료");

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchy roleHierarchy = RoleHierarchyImpl.fromHierarchy(
            "ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");

        log.debug("[SecurityConfig] RoleHierarchy 설정 완료: {}",
            "ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");

        return roleHierarchy;
    }

    /* Method Security에서 RoleHierarchy를 사용하기 위한 설정
     *  @PreAuthorize 등에서 권한 계층을 인식할 수 있도록 해준다.
     * */
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();

        handler.setRoleHierarchy(roleHierarchy);

        log.debug("[SecurityConfig] MethodSecurityExpressionHandler 설정 완료");

        return handler;
    }
}
