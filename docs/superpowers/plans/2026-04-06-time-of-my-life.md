# time-of-my-life Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a native Android app that tracks personal finances and shows how many months you can live on current assets under different scenarios.

**Architecture:** Single-module Android app with `data` (Room + JSON), `domain` (pure computation), and `ui` (Compose + ViewModels) layers. Manual dependency injection via Application class. Room for persistence, kotlinx.serialization for JSON import/export via Android Storage Access Framework.

**Tech Stack:** Kotlin, Jetpack Compose, Material3, Room 2.6.1, kotlinx.serialization 1.7.3, Navigation Compose, Lifecycle ViewModel Compose, KSP, Gradle 8.10, AGP 8.5.2

---

## File Map

```
settings.gradle.kts
build.gradle.kts
gradle/libs.versions.toml
gradle/wrapper/gradle-wrapper.properties
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/java/com/timeofmylife/
  TimeOfMyLifeApp.kt
  MainActivity.kt
  data/
    model/Balance.kt          — entity + Reliability enum
    model/BudgetItem.kt       — entity + ItemType enum
    db/Converters.kt          — Room TypeConverters for enums
    db/BalanceDao.kt
    db/BudgetItemDao.kt
    db/AppDatabase.kt
    json/AppDataExport.kt     — @Serializable mirrors + toExport/toEntity extensions
    json/ImportExportHandler.kt — SAF read/write logic
    FinanceRepository.kt
  domain/
    LifetimeCalculator.kt     — pure computation, no Android deps
  ui/
    theme/Color.kt
    theme/Theme.kt
    AppNavigation.kt          — NavHost + BottomNavigationBar
    balances/BalancesViewModel.kt
    balances/BalancesScreen.kt
    balances/AddEditBalanceDialog.kt
    budget/BudgetViewModel.kt
    budget/BudgetScreen.kt
    budget/AddEditBudgetItemDialog.kt
    lifetime/LifetimeViewModel.kt
    lifetime/LifetimeScreen.kt
app/src/test/java/com/timeofmylife/
  domain/LifetimeCalculatorTest.kt
Makefile
.gitignore
```

---

## Task 1: Project scaffolding

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle/libs.versions.toml`
- Create: `gradle/wrapper/gradle-wrapper.properties`
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `Makefile`
- Create: `.gitignore`

- [ ] **Step 1: Create `settings.gradle.kts`**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "time-of-my-life"
include(":app")
```

- [ ] **Step 2: Create `gradle/libs.versions.toml`**

```toml
[versions]
agp = "8.5.2"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
compose-bom = "2024.11.00"
room = "2.6.1"
lifecycle = "2.8.7"
navigation = "2.8.4"
serialization = "1.7.3"
coroutines = "1.9.0"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-material-icons = { group = "androidx.compose.material", name = "material-icons-extended" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version = "1.9.3" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "serialization" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
junit = { group = "junit", name = "junit", version = "4.13.2" }
kotlin-test-junit = { group = "org.jetbrains.kotlin", name = "kotlin-test-junit", version.ref = "kotlin" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

- [ ] **Step 3: Create `gradle/wrapper/gradle-wrapper.properties`**

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.10-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

- [ ] **Step 4: Download Gradle wrapper**

```bash
gradle wrapper --gradle-version 8.10
```

Expected: `gradle/wrapper/gradle-wrapper.jar` and `gradlew` script created.

- [ ] **Step 5: Create `build.gradle.kts` (root)**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
}
```

- [ ] **Step 6: Create `app/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.timeofmylife"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.timeofmylife"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test.junit)
}
```

- [ ] **Step 7: Create `app/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:name=".TimeOfMyLifeApp"
        android:label="Time of My Life"
        android:theme="@style/Theme.AppCompat"
        android:allowBackup="true"
        android:supportsRtl="true">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- [ ] **Step 8: Create `Makefile`**

```makefile
JAVA_HOME := /usr/lib/jvm/java-17-openjdk
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
```

- [ ] **Step 9: Create `.gitignore`**

```
.gradle/
.idea/
build/
local.properties
*.iml
app/release/
.superpowers/
```

- [ ] **Step 10: Verify project syncs**

```bash
make build
```

Expected: `BUILD SUCCESSFUL` (no source files yet — that's fine; empty module compiles).

- [ ] **Step 11: Commit**

```bash
git add .
git commit -m "scaffold: project structure and build config"
```

---

## Task 2: Data model entities

**Files:**
- Create: `app/src/main/java/com/timeofmylife/data/model/Balance.kt`
- Create: `app/src/main/java/com/timeofmylife/data/model/BudgetItem.kt`
- Create: `app/src/main/java/com/timeofmylife/data/db/Converters.kt`

- [ ] **Step 1: Create `data/model/Balance.kt`**

```kotlin
package com.timeofmylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Reliability { HIGH, MEDIUM, LOW }

@Entity(tableName = "balances")
data class Balance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val reliability: Reliability,
    val amount: Double
)
```

- [ ] **Step 2: Create `data/model/BudgetItem.kt`**

```kotlin
package com.timeofmylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ItemType { EXPENSE, INCOME }

@Entity(tableName = "budget_items")
data class BudgetItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: ItemType,
    val goodAmount: Double,
    val badAmount: Double
)
```

- [ ] **Step 3: Create `data/db/Converters.kt`**

```kotlin
package com.timeofmylife.data.db

import androidx.room.TypeConverter
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.data.model.Reliability

class Converters {
    @TypeConverter fun fromReliability(r: Reliability): String = r.name
    @TypeConverter fun toReliability(s: String): Reliability = Reliability.valueOf(s)
    @TypeConverter fun fromItemType(t: ItemType): String = t.name
    @TypeConverter fun toItemType(s: String): ItemType = ItemType.valueOf(s)
}
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/timeofmylife/data/
git commit -m "feat: data model entities and Room type converters"
```

---

## Task 3: Room DAOs and database

**Files:**
- Create: `app/src/main/java/com/timeofmylife/data/db/BalanceDao.kt`
- Create: `app/src/main/java/com/timeofmylife/data/db/BudgetItemDao.kt`
- Create: `app/src/main/java/com/timeofmylife/data/db/AppDatabase.kt`

- [ ] **Step 1: Create `data/db/BalanceDao.kt`**

```kotlin
package com.timeofmylife.data.db

import androidx.room.*
import com.timeofmylife.data.model.Balance
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balances")
    fun observeAll(): Flow<List<Balance>>

    @Query("SELECT * FROM balances")
    suspend fun getAll(): List<Balance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(balance: Balance): Long

    @Delete
    suspend fun delete(balance: Balance)

    @Query("DELETE FROM balances")
    suspend fun deleteAll()
}
```

- [ ] **Step 2: Create `data/db/BudgetItemDao.kt`**

```kotlin
package com.timeofmylife.data.db

import androidx.room.*
import com.timeofmylife.data.model.BudgetItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetItemDao {
    @Query("SELECT * FROM budget_items")
    fun observeAll(): Flow<List<BudgetItem>>

    @Query("SELECT * FROM budget_items")
    suspend fun getAll(): List<BudgetItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: BudgetItem): Long

    @Delete
    suspend fun delete(item: BudgetItem)

    @Query("DELETE FROM budget_items")
    suspend fun deleteAll()
}
```

- [ ] **Step 3: Create `data/db/AppDatabase.kt`**

```kotlin
package com.timeofmylife.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.BudgetItem

@Database(entities = [Balance::class, BudgetItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
    abstract fun budgetItemDao(): BudgetItemDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance.db"
                ).build().also { instance = it }
            }
    }
}
```

- [ ] **Step 4: Verify build**

```bash
make build
```

Expected: `BUILD SUCCESSFUL` — KSP generates Room code without errors.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/timeofmylife/data/db/
git commit -m "feat: Room DAOs and database"
```

---

## Task 4: JSON export data classes

**Files:**
- Create: `app/src/main/java/com/timeofmylife/data/json/AppDataExport.kt`

- [ ] **Step 1: Create `data/json/AppDataExport.kt`**

```kotlin
package com.timeofmylife.data.json

import com.timeofmylife.data.model.*
import kotlinx.serialization.Serializable

@Serializable
data class BalanceExport(
    val name: String,
    val reliability: String,
    val amount: Double
)

@Serializable
data class BudgetItemExport(
    val name: String,
    val type: String,
    val goodAmount: Double,
    val badAmount: Double
)

@Serializable
data class AppDataExport(
    val balances: List<BalanceExport>,
    val budgetItems: List<BudgetItemExport>
)

fun Balance.toExport() = BalanceExport(name, reliability.name, amount)
fun BudgetItem.toExport() = BudgetItemExport(name, type.name, goodAmount, badAmount)

fun BalanceExport.toEntity() = Balance(
    name = name,
    reliability = Reliability.valueOf(reliability),
    amount = amount
)

fun BudgetItemExport.toEntity() = BudgetItem(
    name = name,
    type = ItemType.valueOf(type),
    goodAmount = goodAmount,
    badAmount = badAmount
)
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/timeofmylife/data/json/AppDataExport.kt
git commit -m "feat: JSON serializable export data classes"
```

---

## Task 5: Repository

**Files:**
- Create: `app/src/main/java/com/timeofmylife/data/FinanceRepository.kt`

- [ ] **Step 1: Create `data/FinanceRepository.kt`**

```kotlin
package com.timeofmylife.data

import com.timeofmylife.data.db.BalanceDao
import com.timeofmylife.data.db.BudgetItemDao
import com.timeofmylife.data.json.*
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.BudgetItem
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val balanceDao: BalanceDao,
    private val budgetItemDao: BudgetItemDao
) {
    val balances: Flow<List<Balance>> = balanceDao.observeAll()
    val budgetItems: Flow<List<BudgetItem>> = budgetItemDao.observeAll()

    suspend fun upsertBalance(balance: Balance) = balanceDao.upsert(balance)
    suspend fun deleteBalance(balance: Balance) = balanceDao.delete(balance)
    suspend fun upsertBudgetItem(item: BudgetItem) = budgetItemDao.upsert(item)
    suspend fun deleteBudgetItem(item: BudgetItem) = budgetItemDao.delete(item)

    suspend fun exportData(): AppDataExport = AppDataExport(
        balances = balanceDao.getAll().map { it.toExport() },
        budgetItems = budgetItemDao.getAll().map { it.toExport() }
    )

    suspend fun importData(data: AppDataExport) {
        balanceDao.deleteAll()
        budgetItemDao.deleteAll()
        data.balances.forEach { balanceDao.upsert(it.toEntity()) }
        data.budgetItems.forEach { budgetItemDao.upsert(it.toEntity()) }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/timeofmylife/data/FinanceRepository.kt
git commit -m "feat: FinanceRepository"
```

---

## Task 6: LifetimeCalculator (TDD)

**Files:**
- Create: `app/src/test/java/com/timeofmylife/domain/LifetimeCalculatorTest.kt`
- Create: `app/src/main/java/com/timeofmylife/domain/LifetimeCalculator.kt`

- [ ] **Step 1: Create failing tests**

Create `app/src/test/java/com/timeofmylife/domain/LifetimeCalculatorTest.kt`:

```kotlin
package com.timeofmylife.domain

import com.timeofmylife.data.model.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LifetimeCalculatorTest {

    private val balances = listOf(
        Balance(name = "Bank", reliability = Reliability.HIGH, amount = 200.0),
        Balance(name = "Broker", reliability = Reliability.MEDIUM, amount = 500.0),
        Balance(name = "Crypto", reliability = Reliability.LOW, amount = 1000.0),
    )
    private val budgetItems = listOf(
        BudgetItem(name = "Food", type = ItemType.EXPENSE, goodAmount = 100.0, badAmount = 250.0),
        BudgetItem(name = "Rent", type = ItemType.EXPENSE, goodAmount = 200.0, badAmount = 300.0),
        BudgetItem(name = "Salary", type = ItemType.INCOME, goodAmount = 600.0, badAmount = 375.0),
    )

    @Test
    fun `returns exactly 6 rows`() {
        val rows = LifetimeCalculator.calculate(balances, budgetItems)
        assertEquals(6, rows.size)
    }

    @Test
    fun `row 0 is H-bad - uses only HIGH balance and bad amounts`() {
        // HIGH = $200
        // expenses bad = $250 + $300 = $550; incomes bad = $375; netBurn = $175
        // balance1m = $200 - $175 = $25
        // monthsLeft = $200 / $175 ≈ 1.14
        val row = LifetimeCalculator.calculate(balances, budgetItems)[0]
        assertEquals(25.0, row.balance1m, 0.01)
        assertEquals(-325.0, row.balance3m, 0.01)
        assertEquals(200.0 / 175.0, row.monthsLeft, 0.01)
    }

    @Test
    fun `row 1 is H-good - netBurn is negative so monthsLeft is infinity`() {
        // HIGH = $200; expenses good = $100 + $200 = $300; incomes good = $600
        // netBurn = $300 - $600 = -$300 (income > expenses)
        // monthsLeft = ∞; balance1m = $200 - (-$300) = $500
        val row = LifetimeCalculator.calculate(balances, budgetItems)[1]
        assertEquals(Double.POSITIVE_INFINITY, row.monthsLeft)
        assertEquals(500.0, row.balance1m, 0.01)
    }

    @Test
    fun `row 4 is HML-bad - uses all balances`() {
        // ALL = $200 + $500 + $1000 = $1700; netBurn bad = $175
        // balance1m = $1700 - $175 = $1525
        // monthsLeft = $1700 / $175 ≈ 9.71
        val row = LifetimeCalculator.calculate(balances, budgetItems)[4]
        assertEquals(1525.0, row.balance1m, 0.01)
        assertEquals(1700.0 / 175.0, row.monthsLeft, 0.01)
    }

    @Test
    fun `empty inputs produce zero balances and infinite months`() {
        val rows = LifetimeCalculator.calculate(emptyList(), emptyList())
        rows.forEach { row ->
            assertEquals(0.0, row.balance1m, 0.0)
            assertEquals(0.0, row.balance12m, 0.0)
            assertEquals(Double.POSITIVE_INFINITY, row.monthsLeft)
        }
    }

    @Test
    fun `row labels are correct`() {
        val rows = LifetimeCalculator.calculate(balances, budgetItems)
        assertEquals("H / bad", rows[0].label)
        assertEquals("H / good", rows[1].label)
        assertEquals("HM / bad", rows[2].label)
        assertEquals("HM / good", rows[3].label)
        assertEquals("HML / bad", rows[4].label)
        assertEquals("HML / good", rows[5].label)
    }
}
```

- [ ] **Step 2: Run tests — verify they fail**

```bash
./gradlew --no-daemon test 2>&1 | tail -20
```

Expected: compilation error — `LifetimeCalculator` not defined yet.

- [ ] **Step 3: Create `domain/LifetimeCalculator.kt`**

```kotlin
package com.timeofmylife.domain

import com.timeofmylife.data.model.*

data class LifetimeRow(
    val label: String,
    val balance1m: Double,
    val balance3m: Double,
    val balance6m: Double,
    val balance12m: Double,
    val monthsLeft: Double   // Double.POSITIVE_INFINITY means income >= expenses
)

object LifetimeCalculator {

    private data class ScenarioDef(
        val tiers: Set<Reliability>,
        val useGood: Boolean,
        val label: String
    )

    private val SCENARIOS = listOf(
        ScenarioDef(setOf(Reliability.HIGH), false, "H / bad"),
        ScenarioDef(setOf(Reliability.HIGH), true, "H / good"),
        ScenarioDef(setOf(Reliability.HIGH, Reliability.MEDIUM), false, "HM / bad"),
        ScenarioDef(setOf(Reliability.HIGH, Reliability.MEDIUM), true, "HM / good"),
        ScenarioDef(setOf(Reliability.HIGH, Reliability.MEDIUM, Reliability.LOW), false, "HML / bad"),
        ScenarioDef(setOf(Reliability.HIGH, Reliability.MEDIUM, Reliability.LOW), true, "HML / good"),
    )

    fun calculate(balances: List<Balance>, budgetItems: List<BudgetItem>): List<LifetimeRow> =
        SCENARIOS.map { scenario ->
            val totalBalance = balances
                .filter { it.reliability in scenario.tiers }
                .sumOf { it.amount }

            val totalExpenses = budgetItems
                .filter { it.type == ItemType.EXPENSE }
                .sumOf { if (scenario.useGood) it.goodAmount else it.badAmount }

            val totalIncomes = budgetItems
                .filter { it.type == ItemType.INCOME }
                .sumOf { if (scenario.useGood) it.goodAmount else it.badAmount }

            val netBurn = totalExpenses - totalIncomes

            val monthsLeft = if (netBurn <= 0) Double.POSITIVE_INFINITY
                             else totalBalance / netBurn

            LifetimeRow(
                label = scenario.label,
                balance1m = totalBalance - netBurn * 1,
                balance3m = totalBalance - netBurn * 3,
                balance6m = totalBalance - netBurn * 6,
                balance12m = totalBalance - netBurn * 12,
                monthsLeft = monthsLeft
            )
        }
}
```

- [ ] **Step 4: Run tests — verify they pass**

```bash
./gradlew --no-daemon test 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL`, all 6 tests pass.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/timeofmylife/domain/ app/src/test/
git commit -m "feat: LifetimeCalculator with unit tests"
```

---

## Task 7: Application class + ImportExportHandler

**Files:**
- Create: `app/src/main/java/com/timeofmylife/TimeOfMyLifeApp.kt`
- Create: `app/src/main/java/com/timeofmylife/data/json/ImportExportHandler.kt`

- [ ] **Step 1: Create `TimeOfMyLifeApp.kt`**

```kotlin
package com.timeofmylife

import android.app.Application
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.db.AppDatabase

class TimeOfMyLifeApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy {
        FinanceRepository(database.balanceDao(), database.budgetItemDao())
    }
}
```

- [ ] **Step 2: Create `data/json/ImportExportHandler.kt`**

```kotlin
package com.timeofmylife.data.json

import android.content.Context
import android.net.Uri
import kotlinx.serialization.json.Json

private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

fun writeExportToUri(context: Context, uri: Uri, data: AppDataExport) {
    context.contentResolver.openOutputStream(uri)?.use { stream ->
        stream.write(json.encodeToString(AppDataExport.serializer(), data).toByteArray())
    }
}

fun readImportFromUri(context: Context, uri: Uri): AppDataExport {
    val text = context.contentResolver.openInputStream(uri)?.use { stream ->
        stream.bufferedReader().readText()
    } ?: error("Cannot read from URI: $uri")
    return json.decodeFromString(AppDataExport.serializer(), text)
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/timeofmylife/TimeOfMyLifeApp.kt \
        app/src/main/java/com/timeofmylife/data/json/ImportExportHandler.kt
git commit -m "feat: Application class and JSON import/export handler"
```

---

## Task 8: Theme

**Files:**
- Create: `app/src/main/java/com/timeofmylife/ui/theme/Color.kt`
- Create: `app/src/main/java/com/timeofmylife/ui/theme/Theme.kt`

- [ ] **Step 1: Create `ui/theme/Color.kt`**

```kotlin
package com.timeofmylife.ui.theme

import androidx.compose.ui.graphics.Color

val Purple = Color(0xFF7C4DFF)
val PurpleContainer = Color(0xFF3D1E99)
val OnPurple = Color(0xFFFFFFFF)

val ExpenseRed = Color(0xFFEF5350)
val IncomeGreen = Color(0xFF66BB6A)
val NegativeText = Color(0xFFEF9A9A)

val HighColor = Color(0xFFA5D6A7)     // green text for HIGH
val MediumColor = Color(0xFFFFCC80)   // orange text for MEDIUM
val LowColor = Color(0xFFEF9A9A)      // red text for LOW

// Lifetime matrix row backgrounds (worst → best, 25% alpha on dark bg)
val LifetimeRowColors = listOf(
    Color(0x40FF0000),  // H/bad   — red
    Color(0x33FF6400),  // H/good  — orange
    Color(0x33FFC800),  // HM/bad  — yellow
    Color(0x3364C800),  // HM/good — green
    Color(0x330064FF),  // HML/bad — blue
    Color(0x338C00FF),  // HML/good— violet
)
```

- [ ] **Step 2: Create `ui/theme/Theme.kt`**

```kotlin
package com.timeofmylife.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    onPrimary = OnPurple,
    primaryContainer = PurpleContainer,
    onPrimaryContainer = OnPurple,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
)

@Composable
fun TimeOfMyLifeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/timeofmylife/ui/theme/
git commit -m "feat: dark theme with purple accent"
```

---

## Task 9: Navigation and MainActivity

**Files:**
- Create: `app/src/main/java/com/timeofmylife/ui/AppNavigation.kt`
- Create: `app/src/main/java/com/timeofmylife/MainActivity.kt`

- [ ] **Step 1: Create `ui/AppNavigation.kt`**

```kotlin
package com.timeofmylife.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.ui.balances.BalancesScreen
import com.timeofmylife.ui.budget.BudgetScreen
import com.timeofmylife.ui.lifetime.LifetimeScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Balances : Screen("balances", "Balances", Icons.Default.AccountBalance)
    object Budget : Screen("budget", "Budget", Icons.Default.AttachMoney)
    object Lifetime : Screen("lifetime", "Life Time", Icons.Default.Timeline)
}

private val screens = listOf(Screen.Balances, Screen.Budget, Screen.Lifetime)

@Composable
fun AppNavigation(repository: FinanceRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Balances.route) {
            composable(Screen.Balances.route) { BalancesScreen(repository, innerPadding) }
            composable(Screen.Budget.route) { BudgetScreen(repository, innerPadding) }
            composable(Screen.Lifetime.route) { LifetimeScreen(repository, innerPadding) }
        }
    }
}
```

- [ ] **Step 2: Create `MainActivity.kt`**

```kotlin
package com.timeofmylife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.timeofmylife.ui.AppNavigation
import com.timeofmylife.ui.theme.TimeOfMyLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as TimeOfMyLifeApp).repository
        setContent {
            TimeOfMyLifeTheme {
                AppNavigation(repository)
            }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/timeofmylife/ui/AppNavigation.kt \
        app/src/main/java/com/timeofmylife/MainActivity.kt
git commit -m "feat: navigation scaffold and MainActivity"
```

---

## Task 10: Balances screen

**Files:**
- Create: `app/src/main/java/com/timeofmylife/ui/balances/BalancesViewModel.kt`
- Create: `app/src/main/java/com/timeofmylife/ui/balances/AddEditBalanceDialog.kt`
- Create: `app/src/main/java/com/timeofmylife/ui/balances/BalancesScreen.kt`

- [ ] **Step 1: Create `balances/BalancesViewModel.kt`**

```kotlin
package com.timeofmylife.ui.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.Reliability
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BalancesViewModel(private val repo: FinanceRepository) : ViewModel() {

    // Groups balances by reliability in declaration order: HIGH, MEDIUM, LOW
    val grouped: StateFlow<Map<Reliability, List<Balance>>> = repo.balances
        .map { list ->
            Reliability.entries.associateWith { r ->
                list.filter { it.reliability == r }.sortedBy { it.name }
            }.filterValues { it.isNotEmpty() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun upsert(balance: Balance) = viewModelScope.launch { repo.upsertBalance(balance) }
    fun delete(balance: Balance) = viewModelScope.launch { repo.deleteBalance(balance) }

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = BalancesViewModel(repo) as T
    }
}
```

- [ ] **Step 2: Create `balances/AddEditBalanceDialog.kt`**

```kotlin
package com.timeofmylife.ui.balances

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.Reliability

@Composable
fun AddEditBalanceDialog(
    initial: Balance?,           // null = adding new
    onConfirm: (Balance) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var reliability by remember { mutableStateOf(initial?.reliability ?: Reliability.HIGH) }
    var amountText by remember { mutableStateOf(initial?.amount?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Balance" else "Edit Balance") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                Text("Reliability", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Reliability.entries.forEach { r ->
                        FilterChip(
                            selected = reliability == r,
                            onClick = { reliability = r },
                            label = { Text(r.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@TextButton
                    if (name.isBlank()) return@TextButton
                    onConfirm(Balance(id = initial?.id ?: 0, name = name.trim(), reliability = reliability, amount = amount))
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
```

- [ ] **Step 3: Create `balances/BalancesScreen.kt`**

```kotlin
package com.timeofmylife.ui.balances

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.Reliability
import com.timeofmylife.ui.theme.HighColor
import com.timeofmylife.ui.theme.LowColor
import com.timeofmylife.ui.theme.MediumColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalancesScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BalancesViewModel = viewModel(factory = BalancesViewModel.Factory(repository))
    val grouped by vm.grouped.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<Balance?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Balances") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add balance")
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = scaffoldPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 16.dp, end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            grouped.forEach { (reliability, items) ->
                stickyHeader(key = reliability.name) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Text(
                            text = when (reliability) {
                                Reliability.HIGH -> "High reliability"
                                Reliability.MEDIUM -> "Medium reliability"
                                Reliability.LOW -> "Low reliability"
                            },
                            color = when (reliability) {
                                Reliability.HIGH -> HighColor
                                Reliability.MEDIUM -> MediumColor
                                Reliability.LOW -> LowColor
                            },
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
                items(items, key = { it.id }) { balance ->
                    BalanceItem(
                        balance = balance,
                        onEdit = { editTarget = balance },
                        onDelete = { vm.delete(balance) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditBalanceDialog(
            initial = null,
            onConfirm = { vm.upsert(it); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }
    editTarget?.let { target ->
        AddEditBalanceDialog(
            initial = target,
            onConfirm = { vm.upsert(it); editTarget = null },
            onDismiss = { editTarget = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BalanceItem(
    balance: Balance,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = {}, onLongClick = onEdit)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(balance.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "$${balance.amount.toLong()}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

- [ ] **Step 4: Verify build**

```bash
make build
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/timeofmylife/ui/balances/
git commit -m "feat: Balances screen with grouped list, add/edit/delete"
```

---

## Task 11: Budget screen

**Files:**
- Create: `app/src/main/java/com/timeofmylife/ui/budget/BudgetViewModel.kt`
- Create: `app/src/main/java/com/timeofmylife/ui/budget/AddEditBudgetItemDialog.kt`
- Create: `app/src/main/java/com/timeofmylife/ui/budget/BudgetScreen.kt`

- [ ] **Step 1: Create `budget/BudgetViewModel.kt`**

```kotlin
package com.timeofmylife.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.BudgetItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudgetViewModel(private val repo: FinanceRepository) : ViewModel() {

    val items = repo.budgetItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun upsert(item: BudgetItem) = viewModelScope.launch { repo.upsertBudgetItem(item) }
    fun delete(item: BudgetItem) = viewModelScope.launch { repo.deleteBudgetItem(item) }

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = BudgetViewModel(repo) as T
    }
}
```

- [ ] **Step 2: Create `budget/AddEditBudgetItemDialog.kt`**

```kotlin
package com.timeofmylife.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType

@Composable
fun AddEditBudgetItemDialog(
    initial: BudgetItem?,
    onConfirm: (BudgetItem) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: ItemType.EXPENSE) }
    var goodText by remember { mutableStateOf(initial?.goodAmount?.toString() ?: "") }
    var badText by remember { mutableStateOf(initial?.badAmount?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Budget Item" else "Edit Budget Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ItemType.entries.forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                OutlinedTextField(
                    value = goodText,
                    onValueChange = { goodText = it },
                    label = { Text("Good amount (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = badText,
                    onValueChange = { badText = it },
                    label = { Text("Bad amount (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val good = goodText.toDoubleOrNull() ?: return@TextButton
                    val bad = badText.toDoubleOrNull() ?: return@TextButton
                    if (name.isBlank()) return@TextButton
                    onConfirm(BudgetItem(id = initial?.id ?: 0, name = name.trim(), type = type, goodAmount = good, badAmount = bad))
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
```

- [ ] **Step 3: Create `budget/BudgetScreen.kt`**

```kotlin
package com.timeofmylife.ui.budget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.ui.theme.ExpenseRed
import com.timeofmylife.ui.theme.IncomeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory(repository))
    val items by vm.items.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<BudgetItem?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Budget") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add budget item")
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = scaffoldPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 16.dp, end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { item ->
                BudgetItemRow(
                    item = item,
                    onEdit = { editTarget = item },
                    onDelete = { vm.delete(item) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddEditBudgetItemDialog(
            initial = null,
            onConfirm = { vm.upsert(it); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }
    editTarget?.let { target ->
        AddEditBudgetItemDialog(
            initial = target,
            onConfirm = { vm.upsert(it); editTarget = null },
            onDismiss = { editTarget = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BudgetItemRow(
    item: BudgetItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (item.type == ItemType.EXPENSE) ExpenseRed else IncomeGreen
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = {}, onLongClick = onEdit)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored left border
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(60.dp)
                        .background(borderColor)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(item.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = item.type.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = borderColor
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("good $${item.goodAmount.toLong()}", style = MaterialTheme.typography.bodySmall, color = IncomeGreen)
                        Text("bad $${item.badAmount.toLong()}", style = MaterialTheme.typography.bodySmall, color = ExpenseRed)
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 4: Verify build**

```bash
make build
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/timeofmylife/ui/budget/
git commit -m "feat: Budget screen with colored-border list, add/edit/delete"
```

---

## Task 12: Lifetime screen

**Files:**
- Create: `app/src/main/java/com/timeofmylife/ui/lifetime/LifetimeViewModel.kt`
- Create: `app/src/main/java/com/timeofmylife/ui/lifetime/LifetimeScreen.kt`

- [ ] **Step 1: Create `lifetime/LifetimeViewModel.kt`**

```kotlin
package com.timeofmylife.ui.lifetime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.domain.LifetimeCalculator
import com.timeofmylife.domain.LifetimeRow
import kotlinx.coroutines.flow.*

class LifetimeViewModel(repo: FinanceRepository) : ViewModel() {

    val rows: StateFlow<List<LifetimeRow>> = combine(repo.balances, repo.budgetItems) { balances, items ->
        LifetimeCalculator.calculate(balances, items)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = LifetimeViewModel(repo) as T
    }
}
```

- [ ] **Step 2: Create `lifetime/LifetimeScreen.kt`**

```kotlin
package com.timeofmylife.ui.lifetime

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.domain.LifetimeRow
import com.timeofmylife.ui.theme.LifetimeRowColors
import com.timeofmylife.ui.theme.NegativeText

private val COLUMNS = listOf("Scenario", "1m", "3m", "6m", "12m", "left")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifetimeScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: LifetimeViewModel = viewModel(factory = LifetimeViewModel.Factory(repository))
    val rows by vm.rows.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Life Time") }) },
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())
                .horizontalScroll(rememberScrollState())
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header row
            Row {
                COLUMNS.forEachIndexed { i, col ->
                    HeaderCell(col, if (i == 0) 120.dp else 70.dp)
                }
            }
            HorizontalDivider()

            rows.forEachIndexed { index, row ->
                MatrixRow(row, LifetimeRowColors.getOrElse(index) { Color.Transparent })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        textAlign = if (text == "Scenario") TextAlign.Start else TextAlign.End,
        modifier = Modifier
            .width(width)
            .padding(vertical = 6.dp, horizontal = 4.dp)
    )
}

@Composable
private fun MatrixRow(row: LifetimeRow, background: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(color = background, modifier = Modifier.fillMaxWidth()) {
            Row {
                Cell(row.label, 120.dp, isLabel = true)
                Cell(formatBalance(row.balance1m), 70.dp)
                Cell(formatBalance(row.balance3m), 70.dp)
                Cell(formatBalance(row.balance6m), 70.dp)
                Cell(formatBalance(row.balance12m), 70.dp)
                Cell(formatMonths(row.monthsLeft), 70.dp)
            }
        }
    }
}

@Composable
private fun Cell(text: String, width: androidx.compose.ui.unit.Dp, isLabel: Boolean = false) {
    val isNegative = text.startsWith("-")
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = when {
            isNegative -> NegativeText
            isLabel -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurface
        },
        textAlign = if (isLabel) TextAlign.Start else TextAlign.End,
        modifier = Modifier
            .width(width)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

private fun formatBalance(amount: Double): String = when {
    amount >= 0 -> "$${amount.toLong()}"
    else -> "-$${(-amount).toLong()}"
}

private fun formatMonths(months: Double): String =
    if (months.isInfinite()) "∞" else months.toLong().toString()
```

- [ ] **Step 3: Verify build**

```bash
make build
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Install on device/emulator and smoke-test all 3 screens**

```bash
make install
```

Expected: App launches, bottom nav shows 3 tabs, each screen renders without crash.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/timeofmylife/ui/lifetime/
git commit -m "feat: Lifetime screen with rainbow prediction matrix"
```

---

## Task 13: JSON import/export UI

**Files:**
- Modify: `app/src/main/java/com/timeofmylife/ui/AppNavigation.kt`
- Create: `app/src/main/java/com/timeofmylife/ui/ImportExportMenu.kt`

This adds an overflow menu to the top app bar (visible from any screen) with Export and Import actions.

- [ ] **Step 1: Create `ui/ImportExportMenu.kt`**

```kotlin
package com.timeofmylife.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.json.readImportFromUri
import com.timeofmylife.data.json.writeExportToUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImportExportMenu(repository: FinanceRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var showImportConfirm by remember { mutableStateOf<android.net.Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val data = repository.exportData()
            writeExportToUri(context, uri, data)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) showImportConfirm = uri
    }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Export JSON") },
            onClick = {
                expanded = false
                exportLauncher.launch("finances.json")
            }
        )
        DropdownMenuItem(
            text = { Text("Import JSON") },
            onClick = {
                expanded = false
                importLauncher.launch(arrayOf("application/json", "*/*"))
            }
        )
    }

    showImportConfirm?.let { uri ->
        AlertDialog(
            onDismissRequest = { showImportConfirm = null },
            title = { Text("Import data?") },
            text = { Text("This will replace all current data with the contents of the selected file.") },
            confirmButton = {
                TextButton(onClick = {
                    showImportConfirm = null
                    scope.launch(Dispatchers.IO) {
                        val data = readImportFromUri(context, uri)
                        repository.importData(data)
                    }
                }) { Text("Replace") }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirm = null }) { Text("Cancel") }
            }
        )
    }
}
```

- [ ] **Step 2: Update `AppNavigation.kt` — add `ImportExportMenu` to every top app bar**

Replace the `NavHost` block's `Scaffold` in `AppNavigation.kt` so that each screen's `TopAppBar` has the menu. The simplest way is to add a top-level `TopAppBar` in `AppNavigation` itself and remove the per-screen `TopAppBar`:

Replace the entire `AppNavigation` composable with:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(repository: FinanceRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = screens.firstOrNull { it.route == currentRoute }?.label ?: "Time of My Life"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = { ImportExportMenu(repository) }
            )
        },
        bottomBar = {
            NavigationBar {
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Balances.route) {
            composable(Screen.Balances.route) { BalancesScreen(repository, innerPadding) }
            composable(Screen.Budget.route) { BudgetScreen(repository, innerPadding) }
            composable(Screen.Lifetime.route) { LifetimeScreen(repository, innerPadding) }
        }
    }
}
```

- [ ] **Step 3: Remove `TopAppBar` from each individual screen**

In `BalancesScreen.kt`, `BudgetScreen.kt`, and `LifetimeScreen.kt`: remove the `topBar = { TopAppBar(...) }` from their `Scaffold` calls, and change `scaffoldPadding.calculateTopPadding()` to `innerPadding.calculateTopPadding()` in their `contentPadding`. Remove the `Scaffold` wrapper entirely from each screen and use a plain `LazyColumn` / `Column` directly with `innerPadding`.

Updated `BalancesScreen` body (the `Scaffold` wrapper is gone):

```kotlin
@Composable
fun BalancesScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BalancesViewModel = viewModel(factory = BalancesViewModel.Factory(repository))
    val grouped by vm.grouped.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<Balance?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 16.dp, end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            grouped.forEach { (reliability, items) ->
                stickyHeader(key = reliability.name) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Text(
                            text = when (reliability) {
                                Reliability.HIGH -> "High reliability"
                                Reliability.MEDIUM -> "Medium reliability"
                                Reliability.LOW -> "Low reliability"
                            },
                            color = when (reliability) {
                                Reliability.HIGH -> HighColor
                                Reliability.MEDIUM -> MediumColor
                                Reliability.LOW -> LowColor
                            },
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }
                }
                items(items, key = { it.id }) { balance ->
                    BalanceItem(balance = balance, onEdit = { editTarget = balance }, onDelete = { vm.delete(balance) })
                }
            }
        }
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = innerPadding.calculateBottomPadding() + 80.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add balance")
        }
    }

    if (showAddDialog) {
        AddEditBalanceDialog(null, onConfirm = { vm.upsert(it); showAddDialog = false }, onDismiss = { showAddDialog = false })
    }
    editTarget?.let { target ->
        AddEditBalanceDialog(target, onConfirm = { vm.upsert(it); editTarget = null }, onDismiss = { editTarget = null })
    }
}
```

Apply the same pattern to `BudgetScreen` (remove its `Scaffold`, keep `Box` + `LazyColumn` + `FloatingActionButton`) and `LifetimeScreen` (remove its `Scaffold`, keep `Column` directly).

Updated `BudgetScreen` body:

```kotlin
@Composable
fun BudgetScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory(repository))
    val items by vm.items.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<BudgetItem?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 16.dp, end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { item ->
                BudgetItemRow(item = item, onEdit = { editTarget = item }, onDelete = { vm.delete(item) })
            }
        }
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = innerPadding.calculateBottomPadding() + 80.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add budget item")
        }
    }

    if (showAddDialog) {
        AddEditBudgetItemDialog(null, onConfirm = { vm.upsert(it); showAddDialog = false }, onDismiss = { showAddDialog = false })
    }
    editTarget?.let { target ->
        AddEditBudgetItemDialog(target, onConfirm = { vm.upsert(it); editTarget = null }, onDismiss = { editTarget = null })
    }
}
```

Updated `LifetimeScreen` body:

```kotlin
@Composable
fun LifetimeScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: LifetimeViewModel = viewModel(factory = LifetimeViewModel.Factory(repository))
    val rows by vm.rows.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row {
            COLUMNS.forEachIndexed { i, col ->
                HeaderCell(col, if (i == 0) 120.dp else 70.dp)
            }
        }
        HorizontalDivider()
        rows.forEachIndexed { index, row ->
            MatrixRow(row, LifetimeRowColors.getOrElse(index) { Color.Transparent })
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    }
}
```

- [ ] **Step 4: Verify build and install**

```bash
make build && make install
```

Expected: App runs, overflow menu in top bar shows Export/Import options, all 3 screens work.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/timeofmylife/ui/
git commit -m "feat: JSON import/export via overflow menu"
```

---

## Task 14: Final verification

- [ ] **Step 1: Run all unit tests**

```bash
make test
```

Expected: `BUILD SUCCESSFUL`, all tests in `LifetimeCalculatorTest` pass.

- [ ] **Step 2: Manual end-to-end test**

Install on device and verify:
1. Add 3 balances: "My bank" HIGH $200, "My broker" MEDIUM $500, "My crypto wallet" LOW $1000
2. Add expenses: "Food" good $100 bad $250, "Rent" good $200 bad $300
3. Add income: "Salary" good $600 bad $375
4. Open Life Time tab — verify 6 rows appear with correct labels and rainbow backgrounds
5. Verify H/bad row shows `$25` for 1m column (200 - 175 = 25)
6. Verify H/good row shows `∞` for the `left` column
7. Export JSON — open file and verify structure matches spec
8. Delete a balance, import the exported file — verify data is restored

- [ ] **Step 3: Tag**

```bash
git tag v0.1.0
```
