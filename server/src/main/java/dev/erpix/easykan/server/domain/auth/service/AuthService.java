package dev.erpix.easykan.server.domain.auth.service;

import dev.erpix.easykan.server.domain.auth.dto.AuthLoginRequestDto;
import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.token.dto.CreateTokenDto;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.auth.UnsupportedAuthenticationMethodException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserService userService;

    public TokenPairDto loginWithPassword(@NotNull AuthLoginRequestDto requestDto) {
        User user = userService.getByLogin(requestDto.login());

        if (user.getPasswordHash() == null) {
            throw new UnsupportedAuthenticationMethodException("User does not have a password set. " +
                    "Please use OAuth or other authentication methods.");
        }

        if (!passwordEncoder.matches(requestDto.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid login or password.");
        }

        CreateTokenDto accessToken = tokenService.createAccessToken(user.getId());
        CreateTokenDto refreshToken = tokenService.createRefreshToken(user.getId());

        return new TokenPairDto(accessToken.rawToken(), accessToken.duration(),
                refreshToken.rawToken(), refreshToken.duration());
    }

}
