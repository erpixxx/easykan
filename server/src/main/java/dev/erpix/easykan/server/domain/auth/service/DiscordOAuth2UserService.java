package dev.erpix.easykan.server.domain.auth.service;

import dev.erpix.easykan.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiscordOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
            new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String discordId = (String) attributes.get("id");
        String username = (String) attributes.get("username");
        String email = (String) attributes.get("email");

        if (discordId == null || username == null) {
            throw new OAuth2AuthenticationException("Missing required Discord user attributes.");
        }

        // Discord may not return an email, we can work around this by generating a fallback email
        if (email == null) {
            email = discordId + "@discord.com"; // Fallback email
        }

        String providerName = userRequest.getClientRegistration().getRegistrationId();

        userService.loadOrCreateFromOAuth(discordId, providerName, username, username, email);

        return null;
    }

}
