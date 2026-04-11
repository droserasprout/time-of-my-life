# Product Specification

## Overview

A personal finance app that answers one question:
*how many months can I live on my current assets under different scenarios?*
All data is local; JSON and CSV files are the import/export formats.

## Data Model

### Balance

Represents a financial asset (bank account, broker, crypto wallet, etc.).

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | Auto-generated primary key |
| `name` | `String` | User-defined label |
| `reliability` | `Reliability` | `HIGH`, `MEDIUM`, or `LOW` |
| `amount` | `Double` | Current balance |

### BudgetItem

Represents a recurring expense or income with two monthly amount scenarios.

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | Auto-generated primary key |
| `name` | `String` | User-defined label |
| `type` | `ItemType` | `EXPENSE` or `INCOME` |
| `bestAmount` | `Double` | Lower expense or higher income (optimistic) |
| `worstAmount` | `Double` | Higher expense / lower income (pessimistic) |

## Architecture

Single-module Android app (Kotlin, Jetpack Compose, Room, Material 3)
with three layers:

```text
ui/         — Compose screens + ViewModels
domain/     — computation logic (pure Kotlin, no Android deps)
data/       — Room database, DAOs, JSON/CSV serialization, repository
```

**State management:** ViewModel per screen, `StateFlow` exposed to Compose.
**Dependency injection:** Manual (constructor injection).
**Persistence:** Room SQLite (v2).
**Serialization:** `kotlinx.serialization` for JSON; manual CSV via `BufferedReader`/`Writer`.

Four tabs: **Balances** | **Budget** | **Life Time** | **Settings**

## Computation Logic

```text
For each row (reliabilityTiers, scenario):
  totalBalance = sum of balance.amount for balance in reliabilityTiers
  totalExpenses = sum of budgetItem.amount[scenario] for EXPENSE items
  totalIncomes  = sum of budgetItem.amount[scenario] for INCOME items
  netBurn = totalExpenses - totalIncomes

  cell(M months) = totalBalance - netBurn * M
  monthsLeft = if netBurn <= 0 then infinity else floor(totalBalance / netBurn)
```

Six scenarios: three reliability tiers (H, HM, HML) crossed with
two budget scenarios (best, worst).
`scenario.amount` maps `best` to `bestAmount`, `worst` to `worstAmount`.

## Import / Export

### JSON

A single JSON file represents the full app state:

```json
{
  "balances": [
    { "name": "My bank", "reliability": "HIGH", "amount": 200.0 }
  ],
  "budgetItems": [
    { "name": "Food", "type": "EXPENSE",
      "bestAmount": 100.0, "worstAmount": 250.0 }
  ]
}
```

### CSV

Per-table CSV files (one for balances, one for budget items)
with header rows matching entity field names.

### Behavior

- **Export:** writes current DB state to user-chosen file/folder via SAF
- **Import:** reads file, replaces all current data (with confirmation dialog)
- IDs are not serialized; they are regenerated on import

## Build & CI

| Tool | Purpose |
|---|---|
| AGP 8.7.x | Android Gradle Plugin |
| Kotlin 2.0.x | Language |
| ktlint | Kotlin code formatting (via Gradle plugin) |
| detekt | Kotlin static analysis (with baseline for existing code) |
| markdownlint | Markdown linting |

`make all` runs the full CI pipeline (`lint` + `test`).
`make format` auto-formats Kotlin sources.

## Out of Scope (v1)

- Multi-currency support
- Cloud sync
- Charts / graphs beyond the matrix
- Notifications or reminders
- Multiple scenario sets
