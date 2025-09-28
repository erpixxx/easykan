package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.testsupport.config.TestcontainersConfig;
import jakarta.transaction.Transactional;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest
@ActiveProfiles("prod")
@Import(TestcontainersConfig.class)
@Transactional
public @interface IntegrationTest {

}
