plugins {
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.sonarqube") version "7.2.2.6593"
    java
}

group = "com.mcm"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    // Includes JUnit Jupiter + Mockito + Spring test utilities
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Only keep these if you're actually using Testcontainers right now
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}

sonar{
    properties{
        property("sonar.projectKey", "cardvault-backend")
        property("sonar.projectName", "cardvault")
        property("sonar.host.url", "http://localhost:9000")
    }
}

tasks.test {
    useJUnitPlatform()
}