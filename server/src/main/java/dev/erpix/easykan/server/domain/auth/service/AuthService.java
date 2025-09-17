package dev.erpix.easykan.server.domain.auth.service;

import dev.erpix.easykan.server.domain.auth.dto.AuthLoginRequestDto;
import dev.erpix.easykan.server.domain.auth.dto.UserAndTokenPairResponseDto;
import dev.erpix.easykan.server.domain.auth.model.OAuthAccount;
import dev.erpix.easykan.server.domain.auth.repository.OAuthAccountRepository;
import dev.erpix.easykan.server.domain.token.AccessToken;
import dev.erpix.easykan.server.domain.token.RawRefreshToken;
import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.dto.UserResponseDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.auth.UnsupportedAuthenticationMethodException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final OAuthAccountRepository oAuthAccountRepository;

    @Transactional
    public User processOidcLogin(String registrationId, OAuth2User oAuth2User) {
        String providerId = oAuth2User.getName();

        return oAuthAccountRepository.findByProviderIdAndProviderName(providerId, registrationId)
                .map(OAuthAccount::getUser)
                .orElseGet(() -> findOrCreateUserAndLinkAccount(registrationId, oAuth2User));
    }

    public UserAndTokenPairResponseDto loginWithPassword(@NotNull AuthLoginRequestDto requestDto) {
        User user = userService.getByLogin(requestDto.login());

        if (user.getPasswordHash() == null) {
            throw new UnsupportedAuthenticationMethodException("User does not have a password set. " +
                    "Please use OAuth or other authentication methods.");
        }

        if (!passwordEncoder.matches(requestDto.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid login or password.");
        }

        AccessToken accessToken = tokenService.createAccessToken(user.getId());
        RawRefreshToken refreshToken = tokenService.createRefreshToken(user.getId());

        TokenPairDto tokenPairDto = new TokenPairDto(
                accessToken.rawToken(), accessToken.duration(),
                refreshToken.combine(), refreshToken.duration());
        return new UserAndTokenPairResponseDto(UserResponseDto.fromUser(user), tokenPairDto);
    }

    private User findOrCreateUserAndLinkAccount(String registrationId, OAuth2User oidcUser) {
        String email = oidcUser.getAttribute("email");
        if (email == null) {
            throw new IllegalStateException("Email not found from OAuth2 provider");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewOidcUserWithEmail(oidcUser, email));

        OAuthAccount oAuthAccount = OAuthAccount.builder()
                .providerName(registrationId)
                .providerId(oidcUser.getName())
                .user(user)
                .build();
        oAuthAccountRepository.save(oAuthAccount);

        return user;
    }

    private User createNewOidcUserWithEmail(OAuth2User oidcUser, String email) {
        String login = oidcUser.getAttribute("preferred_username");
        if (login == null || login.isBlank() || userRepository.existsByLogin(login)) {
            login = email.split("@")[0] + "_" + UUID.randomUUID().toString().substring(0, 5);
        }

        String displayName = oidcUser.getAttribute("name");
        if (displayName == null || displayName.isBlank()) {
            displayName = oidcUser.getAttribute("nickname");
        }
        if (displayName == null || displayName.isBlank()) {
            displayName = login;
        }

        User newUser = User.builder()
                .login(login)
                .displayName(displayName)
                .email(email)
                .build();

        return userRepository.save(newUser);
    }

}
