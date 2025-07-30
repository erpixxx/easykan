package dev.erpix.easykan;

import dev.erpix.easykan.config.EasyKanConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(
        exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableCaching
@EnableMethodSecurity
@EnableConfigurationProperties(EasyKanConfig.class)
public class EasyKanApplication {

    // Add database health check (remove expired user tokens, etc.)
    public static void main(String[] args) {
        SpringApplication.run(EasyKanApplication.class, args);
    }

}
