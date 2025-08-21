package dev.erpix.easykan.server.testsupport.security;

import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

public class PersistUserExtension implements AfterEachCallback, BeforeEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        SecurityContextHolder.clearContext();
        CacheManager cacheManager = SpringExtension.getApplicationContext(context).getBean(CacheManager.class);
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getElement(), WithPersistedUsers.class)
                .ifPresent(ann -> {
                    User principal = createPersistedUser(context, ann.principal());
                    setAuthentication(principal);
                    Arrays.stream(ann.others())
                            .forEach(o -> createPersistedUser(context, o));
                });
        AnnotationSupport.findAnnotation(context.getElement(), WithPersistedUser.class)
                .ifPresent(ann -> {
                    User user = createPersistedUser(context, ann);
                    setAuthentication(user);
                });
    }

    private User createPersistedUser(ExtensionContext context, WithPersistedUser ann) {
        ApplicationContext appCtx = SpringExtension.getApplicationContext(context);
        UserRepository userRepository = appCtx.getBean(UserRepository.class);
        PasswordEncoder passwordEncoder = appCtx.getBean(PasswordEncoder.class);

        User user = User.builder()
                .login(ann.login())
                .displayName(ann.displayName())
                .email(ann.email())
                .passwordHash(passwordEncoder.encode(ann.password()))
                .permissions(UserPermission.toValue(ann.permissions()))
                .build();

        return userRepository.save(user);
    }

    private void setAuthentication(User user) {
        JpaUserDetails userDetails = new JpaUserDetails(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

}
