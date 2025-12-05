plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.15.0"
}

group = "ru.publisher.obsidian"
version = "0.0.1-SNAPSHOT"
description = "obsidian publish service plugin on kotlin"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val springdocVersion = "2.8.13"
val jacksonYamlVersion = "2.20.0"
val flexmarkVersion = "0.64.8"
val luceneVersion = "10.3.2"
val luceneAnalyzers = "8.11.4"


dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonYamlVersion")
    implementation("com.vladsch.flexmark:flexmark-all:$flexmarkVersion")
    implementation("org.apache.lucene:lucene-core:$luceneVersion")
    implementation("org.apache.lucene:lucene-analyzers-common:$luceneAnalyzers")
    implementation("org.apache.lucene:lucene-queryparser:$luceneVersion")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/src/main/resources/openapi/obsidian-publisher.yaml")
    apiPackage.set("ru.publisher.obsidian.api")
    modelPackage.set("ru.publisher.obsidian.api.dto")
    configOptions.put("interfaceOnly", "false")
    configOptions.put("delegatePattern", "false")
    configOptions.put("useSpringBoot3", "true")
}

sourceSets {
    main {
        kotlin {
            srcDir("${layout.buildDirectory}/generate-resources/main/src")
        }
    }
}