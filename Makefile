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

login:
	@curl -s -X POST http://localhost:8080/api/login \
	  -H "Content-Type: application/json" \
	  -d '{"username": "hexlet@example.com", "password": "qwerty"}' > .token
	@echo "Token saved"
	  
