# Build stage
FROM gradle:8.14.2-jdk21 AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY server/build.gradle.kts ./server/

RUN gradle dependencies --no-daemon

COPY server/src ./server/src

RUN gradle :server:bootJar --no-daemon

# Run stage
FROM openjdk:21-jdk-slim

WORKDIR /app

RUN useradd --create-home --shell /bin/bash easykan

COPY --from=builder /app/server/build/libs/*.jar app.jar

RUN chown -R easykan:easykan /app

USER easykan

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]