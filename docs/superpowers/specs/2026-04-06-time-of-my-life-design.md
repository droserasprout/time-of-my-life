# time-of-my-life — Design Spec

**Date:** 2026-04-06  
**Platform:** Android (native)  
**Stack:** Kotlin + Jetpack Compose, Room SQLite, kotlinx.serialization  

---

## Overview

A personal finance app that answers one question: *how many months can I live on my current assets under different scenarios?* Data mirrors the user's existing Excel spreadsheet. All data is local; JSON file is the import/export format.

---

## Data Model

### Balance

Represents a financial asset (bank account, broker, crypto wallet, etc.).

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | Auto-generated primary key |
| `name` | `String` | User-defined label |
| `reliability` | `Reliability` | `HIGH`, `MEDIUM`, or `LOW` |
| `amount` | `Double` | Current balance in USD |

### BudgetItem

Represents a recurring expense or income with two monthly amount scenarios.

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | Auto-generated primary key |
| `name` | `String` | User-defined label |
| `type` | `ItemType` | `EXPENSE` or `INCOME` |
| `goodAmount` | `Double` | Lower expense or higher income (optimistic) |
| `badAmount` | `Double` | Higher expense or lower income (pessimistic) |

---

## Architecture

Single-module Android app with three layers:

```
ui/         — Compose screens + ViewModels
domain/     — computation logic (pure Kotlin, no Android deps)
data/       — Room database, DAOs, JSON serialization, repository
```

**State management:** ViewModel per screen, `StateFlow` exposed to Compose.  
**Dependency injection:** Manual (constructor injection); Hilt can be added later if the app grows.  
**JSON:** `kotlinx.serialization` with a `@Serializable` mirror of each entity. Import/export via Android Storage Access Framework (file picker).

---

## Screens

### Bottom Navigation

Three tabs: **Balances** | **Budget** | **Life Time**

---

### Balances Screen

Displays all balances grouped by reliability tier.

**Layout:**
- `+` button in top app bar to add a balance
- Three sections with colored headers: `High reliability` (green), `Medium reliability` (orange), `Low reliability` (red)
- Each item row: name on the left, USD amount on the right
- Long-press to edit; swipe-to-dismiss to delete (with undo snackbar)

**Add/Edit dialog fields:** name (text), reliability (radio: HIGH / MED / LOW), amount (number).

---

### Budget Screen

Displays all budget items (expenses and incomes) in a single flat list.

**Layout:**
- `+` button in top app bar to add an item
- Each card has a colored left border: red = expense, green = income
- Card content: name + "Expense"/"Income" type label on the left; `good $X` / `bad $X` stacked on the right
- Long-press to edit; swipe-to-dismiss to delete (with undo snackbar)

**Add/Edit dialog fields:** name (text), type (toggle: Expense / Income), good amount (number), bad amount (number).

---

### Life Time Screen

A scrollable prediction matrix showing remaining balance over time for 6 scenarios.

**Table structure:**

| | 1m | 3m | 6m | 12m | left |
|---|---|---|---|---|---|
| H / bad | | | | | |
| H / good | | | | | |
| HM / bad | | | | | |
| HM / good | | | | | |
| HML / bad | | | | | |
| HML / good | | | | | |

**Column definitions:**
- `1m`, `3m`, `6m`, `12m` — remaining balance after that many months
- `left` — months until balance reaches zero; `∞` if `netBurn ≤ 0`

**Row definitions:**  
Each row specifies which reliability tiers are included and which expense/income scenario to use.

- `H` = HIGH only; `HM` = HIGH + MEDIUM; `HML` = HIGH + MEDIUM + LOW
- `good` = use `goodAmount` for all budget items; `bad` = use `badAmount`

**Row styling:** Rainbow background from worst (row 1, red) to best (row 6, violet):
`rgba(255,0,0,0.25)` → `rgba(255,100,0,0.2)` → `rgba(255,200,0,0.2)` → `rgba(100,200,0,0.2)` → `rgba(0,100,255,0.2)` → `rgba(140,0,255,0.2)`

**Cell styling:** Negative values rendered in red text; positive in default text color.

---

## Computation Logic

```
For each row (reliabilityTiers, scenario):
  totalBalance = sum of balance.amount for balance in reliabilityTiers
  totalExpenses = sum of budgetItem.amount[scenario] for EXPENSE items
  totalIncomes  = sum of budgetItem.amount[scenario] for INCOME items
  netBurn = totalExpenses - totalIncomes

  cell(M months) = totalBalance - netBurn * M
  monthsLeft = if netBurn <= 0 then ∞ else floor(totalBalance / netBurn)
```

`scenario.amount` maps `good` → `goodAmount`, `bad` → `badAmount`.

---

## JSON Import / Export

A single JSON file represents the full app state:

```json
{
  "balances": [
    { "name": "My bank", "reliability": "HIGH", "amount": 200.0 }
  ],
  "budgetItems": [
    { "name": "Food", "type": "EXPENSE", "goodAmount": 100.0, "badAmount": 250.0 }
  ]
}
```

- **Export:** writes current DB state to user-chosen file via SAF
- **Import:** reads file, replaces all current data (with confirmation dialog)
- IDs are not serialized; they are regenerated on import

---

## Theme

- Dark theme throughout
- Primary accent: `#7C4DFF` (deep purple)
- Expense indicator: `#EF5350` (red)
- Income indicator: `#66BB6A` (green)
- Negative cell text: `#EF9A9A`

---

## Out of Scope (v1)

- Multi-currency support
- Cloud sync
- Charts / graphs beyond the matrix
- Notifications or reminders
- Multiple scenario sets
