import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.flywaydb.flyway") version "10.14.0"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.jpa") version "2.0.0"
    kotlin("plugin.allopen") version "2.0.0"
}

group = "com.bulanovdm"
version = "0.0.2"
java.sourceCompatibility = JavaVersion.VERSION_17

val telegramApiVersion: String by project
val springCloudVersion: String by project

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    flywayMigration
}

val flywayMigration = configurations.create("flywayMigration")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("javax.xml.bind:jaxb-api:2.3.1") //fix for jpa
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.hibernate.orm:hibernate-community-dialects")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("jp.kukv:kULID:2.0.0.1")
    implementation("org.telegram:telegrambots-longpolling:$telegramApiVersion")
    implementation("org.telegram:telegrambots-springboot-longpolling-starter:$telegramApiVersion")
    implementation("org.telegram:telegrambots-client:$telegramApiVersion")
    //implementation("org.telegram:telegrambots-extensions:$telegramApiVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.1")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-core")
    flywayMigration("org.postgresql:postgresql")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2:2.2.220")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

flyway {
    configurations = arrayOf("flywayMigration")
    url = "jdbc:postgresql://localhost:5432/piter"
    user = "piter"
    password = "gfhfuhfa"
}

kotlin {
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    jar {
        enabled = false
    }
}
