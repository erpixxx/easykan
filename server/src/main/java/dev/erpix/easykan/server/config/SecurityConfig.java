package dev.erpix.easykan.server.config;

import dev.erpix.easykan.server.config.filter.JwtAuthFilter;
import dev.erpix.easykan.server.domain.auth.security.OidcLoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final EasyKanConfig config;

	private final JwtAuthFilter jwtAuthFilter;

	private final Optional<OidcLoginSuccessHandler> oidcLoginSuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(CsrfConfigurer::disable)
			.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(
					req -> req.requestMatchers("/api/auth/login", "/api/auth/logout", "/api/auth/refresh")
						.permitAll()
						.requestMatchers("/login")
						.permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
						.permitAll()
						.anyRequest()
						.authenticated())
			.cors(Customizer.withDefaults())
			.exceptionHandling(handl -> handl.authenticationEntryPoint(
					(req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())))
			.formLogin(AbstractHttpConfigurer::disable);

		if (config.oidc() != null && config.oidc().enabled()) {
			if (oidcLoginSuccessHandler.isPresent()) {
				http.oauth2Login(oauth -> oauth.successHandler(oidcLoginSuccessHandler.get()))
					.authorizeHttpRequests(req -> req.requestMatchers("/oauth2/**").permitAll());
			}
		}

		return http.build();
	}

	@Bean
	@ConditionalOnProperty(prefix = "easykan.oidc", name = "enabled", havingValue = "true")
	public ClientRegistrationRepository clientRegistrationRepository() {
		var oidc = config.oidc();

		ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("oidc")
			.clientId(oidc.clientId())
			.clientSecret(oidc.clientSecret())
			.issuerUri(oidc.issuerUri())
			.scope(oidc.scopes())
			.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
			.userNameAttributeName(oidc.nameAttribute())
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.build();

		return new InMemoryClientRegistrationRepository(clientRegistration);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NotNull CorsRegistry registry) {
				registry.addMapping("/**")
					.allowedOrigins(config.clientUrl(), config.serverUrl())
					.allowedMethods("*")
					.allowCredentials(true)
					.maxAge(3600);
			}
		};
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration cors = new CorsConfiguration();
		cors.setAllowedOrigins(List.of(config.clientUrl(), config.serverUrl()));
		cors.setAllowedMethods(List.of("*"));
		cors.setAllowedHeaders(List.of("*"));
		cors.setAllowCredentials(true);
		cors.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", cors);
		return source;
	}

}
