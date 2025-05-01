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
    implementation("redis.clients:jedis")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0-M1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.3.0")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.2.5")
    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.openid4java:openid4java:1.0.0")
    implementation("org.apache.httpcomponents:fluent-hc:4.5.14")
    implementation("net.sourceforge.nekohtml:nekohtml:1.9.22")
    implementation("com.code-disaster.steamworks4j:steamworks4j:1.9.0")
    implementation("com.code-disaster.steamworks4j:steamworks4j-server:1.9.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.17")
    // Removed H2 dependency as per requirement to use PostgreSQL everywhere
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
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

// Define build types
enum class BuildType {
    TEST, RELEASE
}

// Default to TEST build if not specified
val buildType = project.findProperty("buildType")?.toString()?.uppercase()?.let {
    try {
        BuildType.valueOf(it)
    } catch (e: IllegalArgumentException) {
        BuildType.TEST
    }
} ?: BuildType.TEST

// Set buildType property for all tasks
tasks.withType<JavaExec> {
    systemProperty("buildType", buildType.toString().lowercase())
}

// Configure WAR and JAR tasks to include buildType
tasks.bootWar {
    manifest {
        attributes["Build-Type"] = buildType.toString().lowercase()
    }
}

tasks.bootJar {
    manifest {
        attributes["Build-Type"] = buildType.toString().lowercase()
    }
}

// Create specific tasks for building WAR files with different property files
tasks.register<org.springframework.boot.gradle.tasks.bundling.BootWar>("testWar") {
    group = "build"
    description = "Builds a WAR file with test properties"
    archiveBaseName.set("arkhamus")
    archiveClassifier.set("test")

    mainClass.set("com.arkhamusserver.arkhamus.Application") // Required for BootWar
    targetJavaVersion.set(JavaVersion.VERSION_17) // Set the required target Java version

    manifest {
        attributes["Build-Type"] = BuildType.TEST.toString().lowercase()
        attributes["Main-Class"] = "com.arkhamusserver.arkhamus.Application" // Optional, already included
    }

    // Set system property for the build process
    doFirst {
        System.setProperty("buildType", BuildType.TEST.toString().lowercase())
    }
}

tasks.register<org.springframework.boot.gradle.tasks.bundling.BootWar>("releaseWar") {    group = "build"
    description = "Builds a WAR file with release properties"
    archiveBaseName.set("arkhamus")
    archiveClassifier.set("release")

    mainClass.set("com.arkhamusserver.arkhamus.Application") // Required for BootWar
    targetJavaVersion.set(JavaVersion.VERSION_17) // Set the required target Java version

    manifest {
        attributes["Build-Type"] = BuildType.RELEASE.toString().lowercase()
        attributes["Main-Class"] = "com.arkhamusserver.arkhamus.Application" // Optional, already included
    }

    // Set system property for the build process
    doFirst {
        System.setProperty("buildType", BuildType.RELEASE.toString().lowercase())
    }

}

// Task to build both test and release WAR files
tasks.register("buildAllWars") {
    group = "build"
    description = "Builds both test and release WAR files"
    dependsOn("testWar", "releaseWar")
}

tasks.withType<Test> {
    useJUnitPlatform()
    // Always use test profile for tests
    systemProperty("spring.profiles.active", "test")
}

tasks.bootBuildImage {
    builder.set("paketobuildpacks/builder-jammy-base:latest")
}

tasks.withType<JavaExec> {
    jvmArgs = listOf("-Djava.library.path=/home/steam/steamworks_sdk/sdk/redistributable_bin")
}
