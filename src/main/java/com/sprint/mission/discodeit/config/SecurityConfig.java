package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.handler.ForbiddenAccessDeniedHandler;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile({"!test", "security-test"})
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final ForbiddenAccessDeniedHandler accessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico").permitAll()
            .requestMatchers("/api/auth/csrf-token", "/api/auth/login", "/api/auth/logout",
                "/api/auth/me").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(HttpMethod.POST, "/actuator/loggers/**").hasRole("ADMIN")
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
            .anyRequest().authenticated())
        .exceptionHandling(ex -> ex
            // 미인증(anonymous) 401, 권한 부족 403 (accessDeniedHandler)
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .accessDeniedHandler(accessDeniedHandler)
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        );
    return http.build();
  }

  @Bean
  public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {

    return args -> {
      int filterSize = filterChain.getFilters().size();

      List<String> filterNames = IntStream.range(0, filterSize)
          .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
              filterChain.getFilters().get(idx).getClass()))
          .toList();

      log.debug("현재 적용된 필터 체인 목록:");
      filterNames.forEach(log::debug);
    };
  }

  @Bean
  public CommandLineRunner debugSecurityBeans(UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder,
      LoginSuccessHandler loginSuccessHandler, LoginFailureHandler loginFailureHandler) {
    return args -> {
      log.debug("UserDetailsService 기본 구현체: {}", userDetailsService.getClass());
      log.debug("PasswordEncoder 기본 구현체: {}", passwordEncoder.getClass());
      log.debug("LoginSuccessHandler 기본 구현체: {}", loginSuccessHandler.getClass());
      log.debug("LoginFailureHandler 기본 구현체: {}", loginFailureHandler.getClass());
    };
  }

  /**
   * 역할 계층 정의: ADMIN > CHANNEL_MANAGER > USER
   */
  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchy hierarchy = RoleHierarchyImpl.fromHierarchy(
        "ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");
    return hierarchy;
  }

  /**
   * Method Security 에 RoleHierarchy 적용.
   */
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }
}
