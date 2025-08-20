package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.handler.CustomSessionExpiredStrategy;
import com.sprint.mission.discodeit.auth.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.auth.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.auth.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.auth.handler.JsonAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/favicon.ico", "/error")
            .requestMatchers("/static/**", "/assets/**");
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        LoginSuccessHandler loginSuccessHandler,
        LoginFailureHandler loginFailureHandler,
        CustomAccessDeniedHandler customAccessDeniedHandler,
        JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint,
        SessionRegistry sessionRegistry,
        PersistentTokenBasedRememberMeServices rememberMeServices
    ) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
                        super.handle(request, response, csrfToken);
                        csrfToken.get();
                    }
                })
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "index.html").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                .requestMatchers("/api/auth/csrf-token").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()

                .anyRequest().authenticated()
            )
            .sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionConcurrency(concurrency -> concurrency
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                    .sessionRegistry(sessionRegistry)
                    .expiredSessionStrategy(new CustomSessionExpiredStrategy())
                )
            )
            .rememberMe(remember -> remember
                .rememberMeServices(rememberMeServices)
                .alwaysRemember(false)
            )
            .formLogin(login -> login
                .loginPage("/api/auth/login")
                .loginProcessingUrl("/api/auth/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()
            )
            .httpBasic(AbstractHttpConfigurer::disable)

            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            );

        return http.build();
    }

    @Bean
    public JdbcTokenRepositoryImpl tokenRepository(DataSource dataSource) {
        log.info("JdbcTokenRepository 생성");
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        log.info("JdbcTokenRepository 설정 완료");
        return tokenRepository;
    }

    @Bean
    public PersistentTokenBasedRememberMeServices rememberMeServices(
        @Value("${spring.security.rememberme.key}") String rememberMeKey,
        UserDetailsService userDetailsService,
        JdbcTokenRepositoryImpl tokenRepository
    ) {
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
            rememberMeKey,
            userDetailsService,
            tokenRepository
        );

        rememberMeServices.setTokenValiditySeconds((int) java.time.Duration.ofDays(7).getSeconds());
        rememberMeServices.setCookieName("remember-me");
        rememberMeServices.setParameter("remember-me");

        log.info("Remember-Me 설정 완료");
        return rememberMeServices;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl() {

            @Override
            public void registerNewSession(String sessionId, Object principal) {
                log.info("새 세션 등록 - 사용자: " + principal + ", 세션ID: " + sessionId);
                super.registerNewSession(sessionId, principal);
                log.debug("현재 활성 세션 수: " + getAllSessions(principal, false).size());
            }

            @Override
            public void removeSessionInformation(String sessionId) {
                log.info("세션 제거 - 세션ID: " + sessionId);
                super.removeSessionInformation(sessionId);
            }

            @Override
            public SessionInformation getSessionInformation(String sessionId) {
                SessionInformation info = super.getSessionInformation(sessionId);
                if (info != null) {
                    log.debug("세션 정보 조회 - 세션ID: " + sessionId + ", 만료됨: " + info.isExpired());
                }
                return info;
            }
        };

        return sessionRegistry;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchy hierarchy = RoleHierarchyImpl.fromHierarchy("""
            ROLE_ADMIN > ROLE_CHANNEL_MANAGER
            ROLE_CHANNEL_MANAGER > ROLE_USER
        """);
        return hierarchy;
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public CommandLineRunner printSecurityFilters(FilterChainProxy filterChainProxy) {
        return args -> {
            int index = 1;
            for (SecurityFilterChain chain : filterChainProxy.getFilterChains()) {
                System.out.println("=== Security Filter Chain " + index++ + " ===");
                for (jakarta.servlet.Filter filter : chain.getFilters()) {
                    System.out.println(filter.getClass().getName());
                }
            }
        };
    }
}