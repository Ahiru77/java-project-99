plugins {
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "7.2.2.6593"
	id("io.freefair.lombok") version "8.13.1"
	id("io.sentry.jvm.gradle") version "6.0.0"
	jacoco
	checkstyle
	application
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"


java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

application {
	mainClass.set("hexlet.code.AppApplication")
}

dependencies {
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.1")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.postgresql:postgresql:42.7.2")
	implementation("org.instancio:instancio-junit:4.8.1")
	implementation("net.javacrumbs.json-unit:json-unit-assertj:3.3.0")
	implementation("net.datafaker:datafaker:2.2.2")
	runtimeOnly("com.h2database:h2")

	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// JUnit
	testImplementation(platform("org.junit:junit-bom:5.12.2"))
	testImplementation("org.junit.jupiter:junit-jupiter")

	//Mapstruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	// Spring Doc
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }

sonar {
	properties {
		property("sonar.projectKey", "Ahiru77_java-project-99")
		property("sonar.organization", "ahiru77")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sentry {
	includeSourceContext = true
	org = "ahiru77"
	projectName = "java-spring-boot"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")
}

tasks.named("sentryBundleSourcesJava").configure {
	enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}