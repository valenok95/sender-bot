plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.5"
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.telegram:telegrambots-spring-boot-starter:6.7.0")
    implementation("org.telegram:telegrambots-abilities:6.7.0")
    implementation("pl.allegro.finance:tradukisto:1.0.1")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.11.0")
    implementation ("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
    implementation ("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")
    implementation(group = "org.jsoup", name = "jsoup", version = "1.15.3")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")
}

application {
    // Define the main class for the application.
    mainClass.set("senderbot.SenderBotApplication")
}

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
