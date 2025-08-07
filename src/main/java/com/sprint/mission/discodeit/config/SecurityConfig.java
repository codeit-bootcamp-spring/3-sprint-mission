package com.sprint.mission.discodeit.config;

import java.util.List;
import java.util.stream.IntStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// Csrf 설정
			.csrf(csrf -> csrf
				// Csrf 토큰을 Cookie 기반으로 설정
				// Csrf 토큰 접근을 위해 HttpOnly -> false
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				// CsrfTokenRequestHandler 구현체 CsrfTokenRequestAttributeHandler로 설정
				.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
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

			System.out.println("현재 적용된 필터 체인 목록:");
			filterNames.forEach(System.out::println);
		};
	}

}
