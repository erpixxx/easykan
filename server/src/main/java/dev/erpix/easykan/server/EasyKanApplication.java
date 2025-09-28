package dev.erpix.easykan.server;

import dev.erpix.easykan.server.config.EasyKanConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableCaching
@EnableMethodSecurity
@EnableConfigurationProperties(EasyKanConfig.class)
public class EasyKanApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyKanApplication.class, args);
	}

}
