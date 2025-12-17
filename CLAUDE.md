# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application project called "secure-data" with package name `com.ksign.secure_data`. The project uses:
- Kotlin as the primary language
- Android Gradle Plugin 8.13.2
- Kotlin 2.0.21
- Target SDK 36, minimum SDK 24
- AndroidX libraries with Material Design

## Build Commands

- **Build the project**: `./gradlew build`
- **Build debug APK**: `./gradlew assembleDebug` 
- **Build release APK**: `./gradlew assembleRelease`
- **Clean build**: `./gradlew clean`
- **Install debug on connected device**: `./gradlew installDebug`

## Testing Commands

- **Run unit tests**: `./gradlew test`
- **Run instrumented tests**: `./gradlew connectedAndroidTest`
- **Run specific unit test**: `./gradlew testDebugUnitTest --tests "com.ksign.secure_data.ExampleUnitTest.addition_isCorrect"`
- **Run specific instrumented test**: `./gradlew connectedDebugAndroidTest --tests "com.ksign.secure_data.ExampleInstrumentedTest.useAppContext"`

## Project Structure

- **app/src/main/java/com/ksign/secure_data/**: Main application code (currently empty - only has default template)
- **app/src/test/**: Unit tests that run on JVM
- **app/src/androidTest/**: Instrumented tests that run on Android devices/emulators
- **app/src/main/res/**: Android resources (layouts, strings, drawables, etc.)
- **gradle/libs.versions.toml**: Version catalog for dependency management

## Development Notes

- The project currently contains only template/example code with no actual application logic implemented
- ProGuard is configured but disabled for release builds (isMinifyEnabled = false)
- Backup and data extraction rules are configured in XML resources
- Uses Java 11 compatibility and Kotlin JVM target 11