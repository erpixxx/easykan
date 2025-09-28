package dev.erpix.easykan.server;

import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.config.TestcontainersConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Tag(Category.INTEGRATION_TEST)
@SpringBootTest
@Import(TestcontainersConfig.class)
@TestPropertySource(properties = { "easykan.jwt.secret=SuperSecretKeyForTestsDontUseInProduction_CatsArePrettyCute",
		"easykan.client-url=http://localhost:8080", "easykan.server-url=http://localhost:8080" })
class EasyKanApplicationTests {

	@Test
	void contextLoads() {
	}

}
