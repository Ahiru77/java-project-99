plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "7.2.2.6593"
	id("io.freefair.lombok") version "8.13.1"
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
