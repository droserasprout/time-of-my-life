# Time of My Life

Android app that answers one question: **how long can you live on your current savings?**

Enter your balances and monthly budget, and the app computes how many months (and the exact final date) your money lasts across six scenarios — from your most reliable assets with an optimistic budget to everything you own in the worst case.

## Screens

### Balances
Your assets, grouped by reliability: **High** (e.g. bank account), **Medium** (e.g. brokerage), **Low** (e.g. crypto). Swipe left to delete, long-press to edit.

### Budget
Monthly expenses and incomes. Each item has a **best** amount (optimistic scenario) and a **worst** amount (pessimistic). Totals and net are pinned at the top. Sort A–Z or by size. Swipe left to delete with confirmation.

### Life Time
Two tables:

**Balance matrix** — how much money remains after 1, 3, 6, and 12 months across six scenarios (high/medium/low reliability × best/worst budget).

**Survival table** — for each scenario: time left formatted as `Xm Yd` and the projected final day as `YYYY-MM-DD`.

Scenarios where income ≥ expenses show `∞`.

## Data

All data is stored locally in a SQLite database (Room). No network access, no accounts.

Use the **⋮ menu** in the top bar to export or import all data as JSON.

## Build

```bash
# Debug APK
make build

# Install on connected device
make install

# Unit tests
make test
```

Requires Android SDK (set `ANDROID_HOME`) and JDK 17 (set `JAVA_HOME`). See `Makefile` for defaults.

Minimum SDK: 26 (Android 8.0). Target SDK: 35.
