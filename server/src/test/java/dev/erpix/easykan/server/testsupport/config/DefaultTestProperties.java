package dev.erpix.easykan.server.testsupport.config;

import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestPropertySource(properties = { "easykan.jwt.secret=testsecretkeyforjwtwhichisverysecure",
		"easykan.jwt.access-token-expire=60", "easykan.jwt.refresh-token-expire=900", "easykan.password.enabled=true",
		"easykan.password.min-length=8", "easykan.password.require-uppercase=true",
		"easykan.password.require-lowercase=true", "easykan.password.require-digit=true",
		"easykan.password.require-special-character=true", "easykan.client-url=http://localhost",
		"easykan.server-url=http://localhost:8080", "easykan.create-default-admin-account=false",
		"easykan.oidc.enabled=false" })
public @interface DefaultTestProperties {

}
