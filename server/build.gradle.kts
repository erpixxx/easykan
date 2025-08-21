import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.erpix.easykan.server"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.bucket4j:bucket4j_jdk17-core:8.14.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.2")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation("org.flywaydb:flyway-core:11.10.4")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.10.4")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-cache:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-security:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter-web:3.5.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc:3.0.4")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")

}

tasks.withType<BootRun> {
    systemProperties["spring.output.ansi.enabled"] = "ALWAYS"
    sourceResources(sourceSets["main"])
}

tasks.withType<Test> {
    doFirst {
        val agentJar = configurations.testRuntimeClasspath.get().files.find {
            it.name.contains("byte-buddy-agent")
        } ?: throw GradleException("Byte Buddy agent not found in test runtime classpath")

        jvmArgs("-javaagent:${agentJar.absolutePath}")
    }
    useJUnitPlatform()
}

tasks.register<Test>("unitTests") {
    useJUnitPlatform {
        includeTags("unit-test")
    }
}

tasks.register<Test>("integrationTests") {
    useJUnitPlatform {
        includeTags("integration-test")
    }
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    archiveFileName.set("easykan-server.jar")
}

