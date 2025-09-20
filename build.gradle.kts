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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
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
    configOptions.put("interfaceOnly", "false")  // генерируем классы, а не только интерфейсы
    configOptions.put("delegatePattern", "false") // генерируем @RestController напрямую
    configOptions.put("useSpringBoot3", "true")   // для Spring Boot 3
}

sourceSets {
    main {
        kotlin {
            srcDir("${layout.buildDirectory}/generate-resources/main/src")
        }
    }
}