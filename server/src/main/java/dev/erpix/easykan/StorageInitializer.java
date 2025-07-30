package dev.erpix.easykan;

import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

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
                    .isAdmin(true)
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
