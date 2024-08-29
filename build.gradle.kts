import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
    war
}

group = "com.arkhamus-server"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-all:4.1.101.Final")
    implementation("org.springframework:spring-core:6.1.7")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter:3.2.1")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.session:spring-session-core")
    implementation("org.springframework.boot:spring-boot-starter-parent:3.2.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6:3.1.2.RELEASE")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.2.1")
    implementation("redis.clients:jedis:5.1.5")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0-M1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.3.0")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.2.5")
    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.openid4java:openid4java:1.0.0")
    implementation("org.apache.httpcomponents:fluent-hc:4.5.14")
    implementation("net.sourceforge.nekohtml:nekohtml:1.9.22")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.war {
    archiveBaseName.value("arkhamus")
    manifest {
        attributes["Main-Class"] = "com.arkhamusserver.arkhamus.Application"
    }
}

tasks.jar {
    archiveBaseName.value("arkhamus")
    manifest {
        attributes["Main-Class"] = "com.arkhamusserver.arkhamus.Application"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootBuildImage {
    builder.set("paketobuildpacks/builder-jammy-base:latest")
}

