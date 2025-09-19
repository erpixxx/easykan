package dev.erpix.easykan.server;

import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// TODO: This is a temporary solution to initialize the storage with a default admin user.
@Component
@ConditionalOnProperty(name = "easykan.create-default-admin-account", havingValue = "true",
        matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class StorageInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing storage...");
        if (userRepository.count() == 0) {
            String passwd = generateRandomPassword(16);

            User admin = User.builder().login("admin").displayName("Administrator")
                    .email("admin@example.com").passwordHash(passwordEncoder.encode(passwd))
                    .permissions(1L).build();

            userRepository.save(admin);

            log.info("========================================");
            log.info("Storage initialized with default admin user.");
            log.info("");
            log.info("Admin login: {}", admin.getLogin());
            log.info("Admin password: {}", passwd);
            log.info("=========================================");
        }
    }

    private String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
