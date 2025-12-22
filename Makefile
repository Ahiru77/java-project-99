run-dist:
	./build/install/app/bin/app
run:
	./gradlew run
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