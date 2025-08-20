package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.handler.AuthenticationEntryPointHandler;
import com.sprint.mission.discodeit.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.handler.LoginSuccessHandler;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
        LoginSuccessHandler loginSuccessHandler,
        LoginFailureHandler loginFailureHandler,
        AuthenticationEntryPointHandler authenticationEntryPointHandler,
        CustomAccessDeniedHandler customAccessDeniedHandler,
        SessionRegistry sessionRegistry) throws Exception {
        System.out.println("[SecurityConfig] FilterChain 구성 시작");
        http
            // CSRF 설정 - 쿠키기반
            .csrf(csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            // 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                    // 인증 제외
                    .requestMatchers(
                        "/",
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/auth/csrf-token",
                        "/api/users",

                        "/swagger-ui/**",
                        "/v3/api-docs/**"

//                        "/index.html",
//                        "/favicon.ico",
//                        "/index-*.js",
//                        "/index-*.css",
//                        "/assets/**",
                    ).permitAll()
                    // 그 외 모든 API는 인증 필요
                    .anyRequest().authenticated()

            )
            // Form 기반 로그인 설정
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .permitAll()
            )
            // HTTP Basic 인증 비활성화
            .httpBasic(basic -> basic.disable())
            .exceptionHandling(configurer -> configurer
                // 로그인되지않는 사용자가 요청할 때
                .authenticationEntryPoint(authenticationEntryPointHandler)
                // 로그인은 했지만 권한이 없는 사용자가 요청할 때
                .accessDeniedHandler(customAccessDeniedHandler)
            )
            //동일한 계정으로 동시 로그인할 수 없도록 설정
            .sessionManagement(session -> session
                .sessionConcurrency(concurrency -> concurrency
                    .maximumSessions(1) // 최대 세션 1개
                    .maxSessionsPreventsLogin(true) // 이미 로그인 상태면 새 로그인 차단
                    // 세션 만료 시 실행할 액션
                    .expiredSessionStrategy(new SessionInformationExpiredStrategy() {
                        @Override
                        public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
                            throws IOException, ServletException {
                            log.warn("다른 위치에서 로그인되어 세션이 종료되었습니다.");
                        }
                    })
                    // 추가적으로 권한 변경된 사용자의 세션을 무효화하기 위해 sessionRegistry 사용
                    .sessionRegistry(sessionRegistry)
                )
            )
        //권한이 변경된 사용자가 로그인 상태라면 세션을 무효화

        ;

        return http.build();
    }

    /* 정적 리소스는 필터 체인에서 아예 제외 */
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {

        return (web -> web.ignoring()
            .requestMatchers("/favicon.ico")
            // 정적 리소스 (CSS, JavaScript, 이미지 등)
            .requestMatchers(
                "/static/**",
                "/assets/**",
                "/index.html",
                "/index-*.js",
                "/index-*.css"
            ));
    }


    /* 설명. 필터 체인 디버깅을 위한 Bean 설정
     *  CommandLineRunner를 사용해 애플리케이션 시작 시 필터 체인의 클래스 이름을 출력하여 디버깅 용도로 사용한다.
     * */
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

    /* 설명. 사용자의 비밀번호를 BCrypt 암호화하기 위한 Bean 설정
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*Role Hierarchy 설정. ADMIN 권한은 USER의 모든 권한을 포함*/
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchy hierarchy = RoleHierarchyImpl.fromHierarchy(
            "ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");
        System.out.println(
            "[SecurityConfig] RoleHierarchy 설정 완료: ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");

        return hierarchy;
    }

    /* 설명. Method Security에서 RoleHierarchy를 사용하기 위한 설정 @PreAuthorize 에서 role 계층 인식 가능 */
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        RoleHierarchy roleHierarchy
    ) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        System.out.println("[SecurityConfig] MethodSecurityExpressionHandler 설정 완료");
        return handler;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
