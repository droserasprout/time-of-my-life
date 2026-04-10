JAVA_HOME := /usr/lib/jvm/java-21-openjdk
ANDROID_HOME := $(HOME)/Android/Sdk

export JAVA_HOME
export ANDROID_HOME

.PHONY: $(MAKECMDGOALS)

build:
	./gradlew assembleDebug

install:
	./gradlew installDebug

test:
	./gradlew test

lint-docs:
	markdownlint docs/

clean:
	./gradlew clean
