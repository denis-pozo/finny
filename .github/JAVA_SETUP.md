# Java Environment Setup

This project uses **Java 21.0.8 (Amazon Corretto)** both locally and in CI to ensure consistency.

## Quick Setup (Recommended - SDKMAN)

### 1. Install SDKMAN (if not already installed)

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

### 2. Install Java 21.0.8 (Amazon Corretto)

```bash
sdk install java 21.0.8-amzn
sdk use java 21.0.8-amzn
```

### 3. Use Automatic Version Switching

The project includes a `.sdkmanrc` file that automatically switches to the correct Java version:

```bash
# Enable SDKMAN auto-env feature (add to ~/.zshrc or ~/.bashrc)
echo 'export SDKMAN_AUTO_ENV=true' >> ~/.zshrc

# Now when you cd into the project, Java version switches automatically
cd /path/to/finny
# Automatically switches to Java 21.0.8-amzn
```

## Verify Your Setup

Run the verification script:

```bash
./scripts/verify-java.sh
```

Expected output:
```
✅ Java version matches!
✅ Using Amazon Corretto (recommended)
```

## Manual Verification

Check your Java version:

```bash
java -version
```

Should output:
```
openjdk version "21.0.8" 2024-01-16 LTS
OpenJDK Runtime Environment Corretto-21.0.8.7.1 (build 21.0.8+7-LTS)
OpenJDK 64-Bit Server VM Corretto-21.0.8.7.1 (build 21.0.8+7-LTS, mixed mode, sharing)
```

## Why This Matters

### Local vs CI Consistency

| Environment | Java Version | Distribution |
|-------------|--------------|--------------|
| **Local** (you) | 21.0.8 | Amazon Corretto |
| **CI** (GitHub Actions) | 21.x (latest) | Amazon Corretto |

**Note:** GitHub Actions installs the latest Java 21 available from Corretto (e.g., 21.0.10). Minor/patch differences within Java 21 don't affect build compatibility.

### Benefits

✅ **Reproducible builds** - Same Java version = same behavior
✅ **Consistent test results** - No "works on my machine" issues
✅ **Predictable compilation** - Java compiler behaves identically
✅ **No surprises** - CI won't fail due to Java version differences

## Version Configuration

### Where Java Version is Defined

1. **`.sdkmanrc`** - SDKMAN auto-switches to this version locally
   ```
   java=21.0.8-amzn
   ```

2. **`composeApp/build.gradle.kts`** - Gradle toolchain enforces Java 21
   ```kotlin
   jvmToolchain(21)
   ```

3. **`.github/workflows/ci.yml`** - CI uses latest Java 21
   ```yaml
   java-version: '21'
   distribution: 'corretto'
   ```

4. **`.github/workflows/release.yml`** - Release uses latest Java 21
   ```yaml
   java-version: '21'
   distribution: 'corretto'
   ```

## Alternative Setup (Without SDKMAN)

If you prefer not to use SDKMAN:

### Download Amazon Corretto 21

1. Visit: https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html
2. Download **Amazon Corretto 21.0.8** for your OS
3. Install and set as `JAVA_HOME`

### macOS (Homebrew)

```bash
brew install --cask corretto21
```

### Verify Installation

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-21.jdk/Contents/Home
java -version
```

## Troubleshooting

### Wrong Java Version

```bash
# List installed Java versions (SDKMAN)
sdk list java | grep installed

# Switch to correct version
sdk use java 21.0.8-amzn

# Set as default
sdk default java 21.0.8-amzn
```

### Gradle Uses Different Java

Gradle respects `jvmToolchain(21)` in `build.gradle.kts`, which will automatically download the correct Java if needed.

Check what Gradle is using:
```bash
./gradlew --version
```

### CI Fails But Local Works

1. Run verification script: `./scripts/verify-java.sh`
2. Check GitHub Actions logs for Java version mismatch
3. Ensure `.sdkmanrc` is committed to git
4. Clear Gradle cache: `./gradlew clean`

## Updating Java Version

When upgrading Java (e.g., from 21.0.8 to 21.0.9):

1. Update `.sdkmanrc`
2. Update both workflow files (`.github/workflows/*.yml`)
3. Test locally: `./gradlew clean test`
4. Commit and push
5. Verify CI passes

## References

- [Amazon Corretto Documentation](https://docs.aws.amazon.com/corretto/)
- [SDKMAN Documentation](https://sdkman.io/usage)
- [Gradle Toolchains](https://docs.gradle.org/current/userguide/toolchains.html)
