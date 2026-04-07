## Environment

### OS / Shell
- OS: Linux 6.19.10-1-cachyos (CachyOS / Arch-based)
- Shell: zsh

### Java
- Default runtime: OpenJDK 21.0.10 (`java -version`)
- Installed JVMs under `/usr/lib/jvm/`: `java-17-openjdk`, `java-21-openjdk`, `java-26-jdk`, `java-26-openjdk`
- For Android projects targeting Java 17: set `JAVA_HOME=/usr/lib/jvm/java-17-openjdk` and use `kotlin { jvmToolchain(17) }` in `app/build.gradle.kts`

### Android
- SDK root: `~/Android/Sdk` (`$HOME/Android/Sdk`)
- `adb` available both at `~/Android/Sdk/platform-tools/adb` and `/usr/bin/adb`
- Shell env may have `ANDROID_HOME` set to `~/Android` (without `Sdk/` suffix) — override with `:=` in Makefile: `ANDROID_HOME := $(HOME)/Android/Sdk`

### Build tools
- Gradle: 9.4.1 (system)
- Use `./gradlew --no-daemon` for project-local Gradle wrapper

### Makefile conventions
- Use `.PHONY: $(MAKECMDGOALS)` to avoid per-target `.PHONY` declarations
- Export `JAVA_HOME` and `ANDROID_HOME` from Makefile so Gradle picks them up
