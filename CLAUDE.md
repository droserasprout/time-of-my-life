## Project

Android app (Kotlin, Jetpack Compose, Room, Material 3) that calculates how long savings last across budget scenarios.

### Architecture

- **Data layer**: Room database (v2) with `Balance` and `BudgetItem` entities, `FinanceRepository` as single access point
- **UI layer**: Single-activity, four screens (`Balances`, `Budget`, `Lifetime`, `Settings`) in a `HorizontalPager` with bottom `NavigationBar`
- **Export/import**: JSON (single file) and CSV (folder with per-table files) via SAF (`DocumentFile`)

### Key patterns

- ViewModels expose `StateFlow`; screens collect with `collectAsStateWithLifecycle`
- Colors: blue (`BestBlue`) / orange (`WorstOrange`) for best/worst scenarios; green (`IncomeGreen`) / red (`ExpenseRed`) for income/expense; green/yellow/red for high/medium/low reliability
- `drawBehind` for lightweight decorations (colored borders, tick bars) instead of extra layout passes

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
