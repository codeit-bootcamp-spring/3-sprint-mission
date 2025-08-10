package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.handler.RestAccessDeniedHandler;
import com.sprint.mission.discodeit.security.handler.RestAuthEntryPoint;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	// FilterChain 정의
	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity http,
		LoginSuccessHandler loginSuccessHandler,
		LoginFailureHandler loginFailureHandler,
		RestAuthEntryPoint restAuthEntryPoint,
		RestAccessDeniedHandler restAccessDeniedHandler
	)
		throws Exception {
		http
			// Csrf 설정
			.csrf(csrf -> csrf
				// Csrf 토큰을 Cookie 기반으로 설정
				// Csrf 토큰 접근을 위해 HttpOnly -> false
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				// CsrfTokenRequestHandler 구현체 CsrfTokenRequestAttributeHandler로 설정
				.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
			)
			.formLogin(login -> login
				.loginProcessingUrl("/api/auth/login")
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
				.permitAll()
			)
			.authorizeHttpRequests(auth -> auth
				// 회원가입 모두허용
				.requestMatchers(HttpMethod.POST, "/api/users").permitAll()
				// 로그인 모두허용
				.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
				// 로그아웃 모두허용
				.requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
				// CSRF 발급 모두허용
				.requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
				//"/api/아래의 모든 요청에 대해 검증
				.requestMatchers("/api/**").authenticated()
				// 그 이외는 모두허용
				.anyRequest().permitAll()
			)
			// 로그아웃 설정
			.logout(logout -> logout
				.logoutUrl("/api/auth/logout")  // 로그아웃 엔드포인트 지정
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(
					HttpStatus.NO_CONTENT)) // 응답을 204 NO_CONTENT로 설정
				.invalidateHttpSession(true) // 세션 종료
				.deleteCookies("JSESSIONID") // 쿠키 제거
				.permitAll()
			)
			// 권한 미확인시 예외처리
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(restAuthEntryPoint) // 401 JSON
				.accessDeniedHandler(restAccessDeniedHandler) // 403 JSON
			);
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(
		HttpSecurity http,
		UserDetailsService userDetailsService,
		PasswordEncoder passwordEncoder) throws Exception {

		var authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authManagerBuilder.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder);
		return authManagerBuilder.build();
	}

	// 디버깅을 위한 FilterChain 확인
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

	// PasswordEncoder Bcrypt로 설정
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.fromHierarchy("""
			    ROLE_ADMIN > ROLE_CHANNEL_MANAGER
			    ROLE_CHANNEL_MANAGER > ROLE_USER
			""");
	}

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
		RoleHierarchy roleHierarchy) {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setRoleHierarchy(roleHierarchy);
		System.out.println("[SecurityConfig] MethodSecurityExpressionHandler 설정 완료");
		return handler;
	}

}
