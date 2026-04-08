JAVA_HOME := /usr/lib/jvm/java-21-openjdk
ANDROID_HOME := $(HOME)/Android/Sdk

export JAVA_HOME
export ANDROID_HOME

.PHONY: $(MAKECMDGOALS)

build:
	./gradlew --no-daemon assembleDebug

install:
	./gradlew --no-daemon installDebug

test:
	./gradlew --no-daemon test

clean:
	./gradlew --no-daemon clean
