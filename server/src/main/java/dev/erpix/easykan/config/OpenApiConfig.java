package dev.erpix.easykan.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EasyKan API",
                version = "1.0.0",
                description = "API documentation for EasyKan."
        )
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI();
    }

}
