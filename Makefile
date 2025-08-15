.DEFAULT_GOAL := build

MVN ?= mvn

NAME := $(shell mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION := $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
JAR_NAME := target/$(NAME)-$(VERSION).jar

.PHONY: help build format test clean run jar run-jar

help: # Show available commands
	@echo -e "Usage: make \033[93m<target>\033[0m"
	@echo
	@echo "Available targets:"
	@awk '\
		BEGIN { FS = ":.*#"; } \
		/^[a-zA-Z_-]+:.*#/ { \
			printf "  \033[93m%-20s\033[0m \033[92m%s\033[0m\n", $$1, $$2 \
		}' $(MAKEFILE_LIST)

build: # Compile and package the application (including fat JAR)
	$(MVN) clean install -Ddependency-check.skip=true

format: # Format the code using fmt-maven-plugin
	$(MVN) com.spotify.fmt:fmt-maven-plugin:format

test: # Run tests using Maven Surefire (JUnit 5)
	$(MVN) test

clean: # Remove target directory
	$(MVN) clean

run: # Run the Spring Boot app using spring-boot:run (dev mode)
	$(MVN) spring-boot:run

jar: # Build only the executable JAR (no install, no tests)
	$(MVN) clean package -DskipTests

run-jar: jar # Run the packaged Spring Boot JAR
	java -jar $(JAR_NAME)
