run-dist:
	./build/install/app/bin/app
run:
	 ./gradlew run --args='--spring.profiles.active=application-development'
lint:
	./gradlew checkstyleMain
test:
	./gradlew test
build:
	./gradlew build
	./gradlew checkstyleMain
	./gradlew checkstyleTest
report:	
	./gradlew jacocoTestReport
.PHONY: build