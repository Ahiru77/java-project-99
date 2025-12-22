plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "7.2.2.6593"
	id("io.freefair.lombok") version "8.13.1"
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
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")

	// JUnit
	testImplementation(platform("org.junit:junit-bom:5.12.2"))
	testImplementation("org.junit.jupiter:junit-jupiter")

}

sonar {
	properties {
		property("sonar.projectKey", "Ahiru77_java-project-99")
		property("sonar.organization", "ahiru77")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
