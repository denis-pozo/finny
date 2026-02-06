# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Finny is a Kotlin Multiplatform project targeting Desktop (JVM) using Compose Multiplatform for the UI. The project uses Gradle with Kotlin DSL for build configuration and Java 21 as the toolchain.

## Essential Commands

### Running the Application
```bash
# Run the desktop application
./gradlew :composeApp:run

# Run with hot reload enabled
./gradlew hotRunJvm --mainClass=org.dnais.finny.MainKt

# Run development version with hot reload
./gradlew hotDevJvm
```

### Building
```bash
# Build the project
./gradlew build

# Assemble without tests
./gradlew assemble

# Clean build artifacts
./gradlew clean

# Create JVM JAR
./gradlew jvmJar
```

### Testing
```bash
# Run all tests
./gradlew allTests

# Run JVM-specific tests
./gradlew jvmTest

# Run tests and build
./gradlew check
```

### Hot Reload
```bash
# Reload running applications after code changes
./gradlew reload
```

## Architecture

### Multi-Platform Structure

The project follows Kotlin Multiplatform conventions with source sets organized by target platform:

- **jvmMain**: Desktop (JVM) specific code - contains the main application entry point and platform-specific implementations
- **jvmTest**: JVM-specific test code
- **commonMain**: Shared code across all platforms (though currently targeting only JVM)
- **commonTest**: Shared test code

Note: While the project is set up for Kotlin Multiplatform, it currently only targets Desktop (JVM). The structure allows for future expansion to other platforms.

### Application Entry Point

- Main class: `org.dnais.finny.MainKt` (composeApp/src/jvmMain/kotlin/org/dnais/finny/main.kt:6)
- The application uses `androidx.compose.ui.window.application` to create a desktop window
- The root composable is `App()` defined in composeApp/src/jvmMain/kotlin/org/dnais/finny/App.kt:24

### Compose Desktop Configuration

- Target formats: DMG (macOS), MSI (Windows), DEB (Linux)
- Package name: `org.dnais.finny`
- Package version: `1.0.0`

### Key Dependencies

- Compose Multiplatform (v1.10.0) for UI
- Material3 (v1.10.0-alpha05) for Material Design components
- Kotlin (v2.3.0)
- AndroidX Lifecycle (v2.9.6) for ViewModel and runtime composition
- Kotlinx Coroutines Swing for coroutine support in desktop
- Compose Hot Reload (v1.0.0) for development

## Source Organization

All source code lives under `composeApp/src/` organized by target platform and source type:

```
composeApp/src/
├── jvmMain/
│   ├── kotlin/org/dnais/finny/    # Application code
│   │   ├── main.kt                 # Entry point
│   │   ├── App.kt                  # Root composable
│   │   ├── Greeting.kt             # Example greeting logic
│   │   └── Platform.kt             # Platform-specific utilities
│   └── composeResources/           # Resources (images, etc.)
│       └── drawable/
└── jvmTest/
    └── kotlin/org/dnais/finny/     # Test code
        └── ComposeAppDesktopTest.kt
```

## Build Configuration

- **Root build.gradle.kts**: Applies plugins but does not configure them (they're applied in subprojects)
- **composeApp/build.gradle.kts**: Main module configuration with Kotlin Multiplatform, Compose, and Hot Reload plugins
- **settings.gradle.kts**: Repository configuration and module inclusion
- **gradle/libs.versions.toml**: Version catalog for dependency management

## Resources

Resources are managed through Compose's resource system. Generated resource accessors are available at `finny.composeapp.generated.resources.Res`. Resource files are placed in `composeApp/src/jvmMain/composeResources/`.

## JVM Toolchain

The project uses Java 21 as specified in the build configuration (composeApp/build.gradle.kts:13). Ensure you have Java 21 installed (managed via `.sdkmanrc` for SDKMAN users).
