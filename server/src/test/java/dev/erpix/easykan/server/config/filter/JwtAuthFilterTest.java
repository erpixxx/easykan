package dev.erpix.easykan.server.config.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import dev.erpix.easykan.server.domain.token.security.JwtProvider;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.service.JpaUserDetailsService;
import dev.erpix.easykan.server.exception.auth.InvalidTokenException;
import dev.erpix.easykan.server.testsupport.Category;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag(Category.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

	@InjectMocks
	private JwtAuthFilter jwtAuthFilter;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private JpaUserDetailsService userDetailsService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilterInternal_shouldAuthenticateUser_whenTokenIsValid() throws Exception {
		String accessToken = "access-token";
		UUID userId = UUID.randomUUID();
		JpaUserDetails mockUserDetails = mock(JpaUserDetails.class);
		Cookie accessTokenCookie = new Cookie("access_token", accessToken);

		when(request.getCookies()).thenReturn(new Cookie[] { accessTokenCookie });
		when(jwtProvider.validate(accessToken)).thenReturn(userId.toString());
		when(userDetailsService.loadUserById(userId)).thenReturn(mockUserDetails);

		jwtAuthFilter.doFilterInternal(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(mockUserDetails);

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void doFilterInternal_shouldNotAuthenticateUser_whenTokenIsInvalid() throws Exception {
		String accessToken = "invalid-token";
		Cookie accessTokenCookie = new Cookie("access_token", accessToken);

		when(request.getCookies()).thenReturn(new Cookie[] { accessTokenCookie });
		when(jwtProvider.validate(accessToken)).thenThrow(new InvalidTokenException());

		jwtAuthFilter.doFilterInternal(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

		verify(filterChain).doFilter(request, response);
		verify(userDetailsService, never()).loadUserById(any(UUID.class));
	}

	@Test
	void doFilterInternal_shouldNotAuthenticateUser_whenNoTokenPresent() throws Exception {
		when(request.getCookies()).thenReturn(null);

		jwtAuthFilter.doFilterInternal(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

		verify(filterChain).doFilter(request, response);
		verify(jwtProvider, never()).validate(anyString());
		verify(userDetailsService, never()).loadUserById(any(UUID.class));
	}

	@Test
	void doFilterInternal_shouldDoNothing_whenUserIsAlreadyAuthenticated() throws Exception {
		Authentication existingAuth = mock(Authentication.class);
		SecurityContextHolder.getContext().setAuthentication(existingAuth);

		Cookie accessTokenCookie = new Cookie("access_token", "access-token");
		when(request.getCookies()).thenReturn(new Cookie[] { accessTokenCookie });

		jwtAuthFilter.doFilterInternal(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);

		verify(filterChain).doFilter(request, response);
		verify(jwtProvider, never()).validate(anyString());
		verify(userDetailsService, never()).loadUserById(any(UUID.class));
	}

}
