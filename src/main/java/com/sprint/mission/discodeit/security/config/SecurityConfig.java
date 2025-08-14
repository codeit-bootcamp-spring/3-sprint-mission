package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.handler.RestAccessDeniedHandler;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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
		RestAccessDeniedHandler restAccessDeniedHandler,
		SessionRegistry sessionRegistry
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
				.authenticationEntryPoint(new Http403ForbiddenEntryPoint()) // 403 응답
				.accessDeniedHandler(restAccessDeniedHandler) // 403 JSON
			)
			// ★★★ 세션 동시성 제어 핵심 블록 ★★★
			.sessionManagement(management -> management
				.sessionConcurrency(concurrency -> concurrency
					.maximumSessions(1) // 동일 사용자 동시 세션 최대 1개
					.maxSessionsPreventsLogin(true) // 새로운 로그인을 통한 세션 생성 거부
					// 기존 세션이 만료될 때의 응답
					.expiredSessionStrategy(event -> {
						var response = event.getResponse();
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setContentType("application/json;charset=UTF-8");
						response.getWriter().write("""
							    {"error":"SESSION_EXPIRED","message":"동일 계정의 다른 로그인으로 세션이 만료되었습니다."}
							""");
					})
					// (4) 누가 어디서 로그인했는지 추적하는 저장소
					.sessionRegistry(sessionRegistry)
				)
			)
		;
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


	// 세션 등록소: 동시 세션 수를 세고 사용자별 세션을 구분
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	// 세션 생성/소멸 이벤트를 스프링으로 전달
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
}