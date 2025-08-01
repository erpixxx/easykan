package dev.erpix.easykan.server;

import dev.erpix.easykan.server.domain.user.model.EKUser;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

// TODO: This is a temporary solution to initialize the storage with a default admin user.
@Component
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

            EKUser admin = EKUser.builder()
                    .login("admin")
                    .displayName("Administrator")
                    .email("admin@example.com")
                    .passwordHash(passwordEncoder.encode(passwd))
                    .permissions(1L)
                    .canAuthWithPassword(true)
                    .build();

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
