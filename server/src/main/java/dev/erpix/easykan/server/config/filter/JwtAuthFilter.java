package dev.erpix.easykan.server.config.filter;

import dev.erpix.easykan.server.domain.token.security.JwtProvider;
import dev.erpix.easykan.server.domain.user.service.JpaUserDetailsService;
import dev.erpix.easykan.server.exception.auth.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	private final JpaUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull FilterChain filterChain) throws ServletException, IOException {

		getTokenFromCookie(request).ifPresent(token -> {
			try {
				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					String subject = jwtProvider.validate(token);
					UUID userId = UUID.fromString(subject);
					UserDetails userDetails = userDetailsService.loadUserById(userId);

					var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
			catch (InvalidTokenException e) {
				log.warn("Invalid JWT token received: {}", e.getMessage());
			}
		});

		filterChain.doFilter(request, response);
	}

	private Optional<String> getTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return Optional.empty();
		}
		return Arrays.stream(cookies)
			.filter(cookie -> cookie.getName().equals("access_token"))
			.map(Cookie::getValue)
			.findFirst();
	}

}
