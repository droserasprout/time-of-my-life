JAVA_HOME := /usr/lib/jvm/java-21-openjdk
ANDROID_HOME := $(HOME)/Android/Sdk

export JAVA_HOME
export ANDROID_HOME

.PHONY: $(MAKECMDGOALS)
MAKEFLAGS += --no-print-directory
##
##  ⏳ Time of My Life
##

help:           ## Show this help (default)
	@grep -Fh "##" $(MAKEFILE_LIST) | grep -Fv grep -F | sed -e 's/\\$$//' | sed -e 's/##//'

##
##-- Build
##

build:          ## Build debug APK
	./gradlew assembleDebug

install:        ## Install debug APK on device
	./gradlew installDebug

clean:          ## Clean build artifacts
	./gradlew clean

##
##-- CI
##

all:            ## Run an entire CI pipeline
	make lint test

test:           ## Run tests
	./gradlew test

lint:           ## Lint with all tools
	./gradlew ktlintCheck detekt
	markdownlint docs/
	markdownlint *.md

format:         ## Format Kotlin sources
	./gradlew ktlintFormat

##
