package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
	public SecurityFilterChain filterChain(HttpSecurity http,
		LoginSuccessHandler loginSuccessHandler, LoginFailureHandler loginFailureHandler)
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
				// 로그인과 CSRF발급만 허용함
				.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
				.anyRequest().authenticated()
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

}
