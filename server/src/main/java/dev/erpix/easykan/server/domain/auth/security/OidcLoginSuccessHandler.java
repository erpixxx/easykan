package dev.erpix.easykan.server.domain.auth.security;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.auth.service.AuthService;
import dev.erpix.easykan.server.domain.token.AccessToken;
import dev.erpix.easykan.server.domain.token.RawRefreshToken;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OidcLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenService tokenService;

	private final AuthService authService;

	private final EasyKanConfig config;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		if (!(authentication instanceof OAuth2AuthenticationToken token)) {
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		String registrationId = token.getAuthorizedClientRegistrationId();
		OAuth2User oidcUser = (OAuth2User) authentication.getPrincipal();

		User user = authService.processOidcLogin(registrationId, oidcUser);

		AccessToken accessToken = tokenService.createAccessToken(user.getId());
		RawRefreshToken refreshToken = tokenService.createRefreshToken(user.getId());

		ResponseCookie accessTokenCookie = ResponseCookie.from(TokenService.ACCESS_TOKEN, accessToken.rawToken())
			.httpOnly(true)
			.secure(config.useHttps())
			.path("/")
			.sameSite("Strict")
			.maxAge(accessToken.duration())
			.build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(TokenService.REFRESH_TOKEN, refreshToken.combine())
			.httpOnly(true)
			.secure(config.useHttps())
			.path("/")
			.sameSite("Strict")
			.maxAge(refreshToken.duration())
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

		clearAuthenticationAttributes(request);

		getRedirectStrategy().sendRedirect(request, response, config.clientUrl());
	}

}
