# Design System

## Colors

### Theme (Dark)

| Token | Hex | Usage |
|-------|-----|-------|
| Primary (Accent) | #9E9E9E | Section titles, selected states, underlines |
| PrimaryContainer | #505050 | Container backgrounds, nav indicator |
| OnPrimary | #FFFFFF | Text on primary |
| Background | #121212 | Screen background |
| Surface | #181818 | Cards, elevated surfaces |
| OnSurface | #E0E0E0 | Primary text |

### Semantic

| Name | Hex | Usage |
|------|-----|-------|
| ExpenseRed | #D4635F | Expense items, borders |
| IncomeGreen | #5BA85E | Income items, borders |
| BestBlue | #3D8FD4 | Best-case amounts, "best" labels |
| WorstOrange | #D9882A | Worst-case amounts, "worst" labels |
| LastGrey | #9E9E9E | Last-month amounts, column headers |
| NegativeText | #EF9A9A | Negative balance values |
| Purple | #7C4DFF | Retained for reference, not used as primary |

### Reliability

| Name | Hex | Usage |
|------|-----|-------|
| HighColor | #A5D6A7 | High reliability text, bar segments |
| MediumColor | #FFCC80 | Medium reliability text, bar segments |
| LowColor | #EF9A9A | Low reliability text, bar segments |
| UncoveredDark | #2A2A2A | Uncovered portion of coverage bars |

### Lifetime Row Backgrounds (25% alpha)

| Scenario | Hex |
|----------|-----|
| H/worst | #40FF0000 |
| H/best | #33FF6400 |
| HM/worst | #33FFC800 |
| HM/best | #3364C800 |
| HML/worst | #330064FF |
| HML/best | #338C00FF |

## Typography

All styles from `MaterialTheme.typography` (Material 3 defaults):

| Style | Used for |
|-------|----------|
| headlineLarge | Welcome screen title |
| headlineMedium | Help screen title |
| titleLarge | Dialog titles |
| titleMedium | Help section headers, ConceptCard titles |
| titleSmall | Settings/ImportExport section headers |
| bodyLarge | Item names, settings item titles, balance amounts |
| bodyMedium | Totals row labels, help text, subtitles |
| bodySmall | Table cell amounts, descriptions, settings subtitles |
| labelMedium | SegmentedSelector options, table headers, list section headers |
| labelSmall | Totals column headers, year labels, bar labels |

## Shapes

| Token | Radius | Usage |
|-------|--------|-------|
| small | 4dp | Bars, small elements |
| medium | 8dp | Item cards |
| large | 12dp | Dialogs, large containers |

## Shared Components

### SlidingNavBar

- Custom bottom bar replacing Material3 NavigationBar
- Height: 56dp, Surface color with 3dp tonal elevation
- Sliding indicator: AccentContainer rounded rect, 8dp corners (matches shapes.medium)
- Tracks HorizontalPager offset for smooth sliding during swipes
- Icons: 22dp, labels: labelSmall
- Selected: Accent color, unselected: onSurface alpha=0.5

### SegmentedSelector

- Padding: horizontal 16dp, vertical 8dp
- Text: `labelMedium`
- Selected: primary color + 2dp underline (Accent)
- Unselected: onSurfaceVariant
- Separator "|": outline color, horizontal padding 8dp
- Item padding: 4dp (symmetric)

### QuickEditDialog

- Card shape: RoundedCornerShape(12dp)
- Column padding: horizontal 16dp, vertical 12dp
- TextField: fillMaxWidth, decimal keyboard, auto-focus, single-line
- Spacer: 8dp between field and buttons
- Buttons: Edit (left) | Cancel + Save (right)

### Full Edit Dialogs (Balance / BudgetItem)

- Card shape: RoundedCornerShape(12dp)
- Column padding: 24dp, spacedBy 12dp
- Title: `titleLarge`
- Field labels: `labelMedium`
- Chip row spacing: 8dp
- Buttons: Delete/error (left, edit mode only) | Cancel + Save (right)

### Item Cards (Balance / BudgetItem)

- Surface shape: `MaterialTheme.shapes.medium`
- tonalElevation: 1dp
- Left border: 4dp wide, drawn via `drawBehind`
- Padding: start 16dp, end 12dp, top 7dp, bottom 7dp
- Name text: `bodyLarge`

### Section Headers (in item lists)

- Text: `labelMedium`
- Color: category color (reliability or expense/income)
- Padding: vertical 4dp

## Screen Layouts

### Balances

```text
Box(fillMaxSize)
  Column(fillMaxSize)
    TotalsPanel (padding: top=innerPadding+4, start/end=16)
      Column (padding: h=12, v=10)
        Proportion bar: height=14, shapes.small, weighted segments
        Spacer(8)
        Header row: "sum" (72dp) + "%" (52dp), labelSmall, LastGrey
        Per-tier rows (HIGH/MEDIUM/LOW): bodyMedium label + bodySmall amounts
    SegmentedSelector (a-z | amount)
    LazyColumn (padding: top=8, bottom=innerPadding+80, start/end=16, spacedBy=8)
      SectionHeader per reliability tier
      BalanceItem cards
  SmallFAB (BottomEnd, shapes.medium, padding: end=16, bottom=innerPadding+16)
```

### Budget

```text
Box(fillMaxSize)
  Column(fillMaxSize)
    TotalsCard (padding: top=innerPadding+4, start/end=16)
      Column (padding: h=12, v=10)
        TickBar: height=14+4 overhang, fullWidth, blue/orange bg, white rounded tick
        Spacer(8)
        Header row: "best"/"last"/"worst" (64dp each), labelSmall
        TotalsRow x3: Expenses/Income/Net, bodyMedium + bodySmall
    SegmentedSelector (a-z | avg | last)
    LazyColumn (padding: top=8, bottom=innerPadding+80, start/end=16, spacedBy=8)
      SectionHeader: "Expenses" / "Income"
      BudgetItemRow cards (3 amount columns, 64dp each)
  SmallFAB (BottomEnd, shapes.medium, padding: end=16, bottom=innerPadding+16)
```

### Life Time

```text
Column(fillMaxSize, padding: top/bottom=innerPadding)
  LifetimeCoverageBar (padding: h=16, v=8)
    YearLabels or "infinity": labelSmall, alpha=0.4, start=40dp offset
    Spacer(2)
    SegmentedBar (worst): label 40dp + bar height=14, shapes.small
    Spacer(4)
    SegmentedBar (best)
    Spacer(6)
  SegmentedSelector (all | expense | income)
  Column(weight=1, verticalScroll)
    Balance table (horizontalScroll, padding: 16)
      HeaderCell: Scenario 120dp + amounts 70dp each, labelMedium, alpha=0.6
      BalanceRow: bodySmall, NegativeText if negative, scenario dots 8dp
      Dividers: outline, alpha=0.2
    Spacer(16)
    Survival table (fillMaxWidth, padding: h=16)
      SurvivalHeaderCell: weight=1f each, labelMedium, alpha=0.6
      SurvivalRow: bodySmall, scenario dots 8dp CircleShape
      Dividers: outline, alpha=0.2
```

### Settings

```text
Column(fillMaxSize, padding: top=innerPadding+16, bottom=innerPadding, start/end=16)
  Section "Display" (labelMedium, primary)
    Demo mode row: bodyLarge + bodySmall, custom AppToggle (44x24dp pill, 18dp thumb)
  Spacer(16)
  Section "Data" (labelMedium, primary)
    SettingsItem: "Import / Export" -> nested ImportExportScreen
  Spacer(16)
  Section "Help" (labelMedium, primary)
    SettingsItem: "Show welcome screen"
    SettingsItem: "Show help"
```

### Import / Export (nested in Settings tab)

```text
Column(fillMaxSize, padding: top=innerPadding+16, bottom=innerPadding, start/end=16)
  Section "Export" (labelMedium, primary)
    3x ImportExportItem (bodyLarge + bodySmall)
  Spacer(16)
  Section "Import" (labelMedium, primary)
    3x ImportExportItem (bodyLarge + bodySmall)
```

### Welcome

```text
Surface(fillMaxSize)
  Column(fillMaxSize, padding: h=32, v=48)
    Column(weight=1, verticalScroll, center)
      Title: headlineLarge, bold, center
      Spacer(8)
      Tagline: bodyLarge, onSurfaceVariant, center
      Spacer(40)
      ConceptCard x3 (Card, Row padding=16, icon 32dp)
        Title: titleMedium, semibold
        Description: bodyMedium, onSurfaceVariant
    OutlinedButton "Show help" (fillMaxWidth)
    Spacer(12)
    Button "Get started" (fillMaxWidth)
```

### Help

```text
Surface(fillMaxSize)
  Column(fillMaxSize, padding: h=24, top=48, bottom=32)
    Column(weight=1, verticalScroll)
      Title: headlineMedium, bold
      Spacer(24)
      SectionTitle x3: titleMedium, semibold, bottom=4dp
      HelpText: bodyMedium, onSurfaceVariant, v=2dp
      BulletItem: bodyMedium, start=8dp, label bold + colored
    Spacer(16)
    Button "Back" (fillMaxWidth)
```

## Label Casing

| Context | Casing | Examples |
|---------|--------|----------|
| Screen/page titles | Sentence case | "Help", "Import / Export" |
| Section headers | Sentence case | "Display", "Data", "Export" |
| Column headers | lowercase | "best", "last", "worst", "sum" |
| Selector options | lowercase | "all", "expense", "a-z", "avg" |
| Dialog buttons | Sentence case | "Save", "Cancel", "Delete" |
| Dialog titles | Sentence case | "Add balance", "Edit budget item" |
| Field labels | Sentence case | "Name", "Best amount (USD)" |
| Nav bar labels | Sentence case | "Balances", "Budget", "Settings" |
| Settings items | Sentence case | "Demo mode", "Import / Export" |
| Buttons | Sentence case | "Get started", "Show help", "Back" |
| Scenario labels | lowercase | "high / worst", "medium / best" |
| List section headers | Sentence case | "High", "Medium", "Expenses" |

Column headers use lowercase; everything else uses sentence case.

## Shared Constants (`Dimensions.kt`)

| Constant | Value | Usage |
|----------|-------|-------|
| ScreenHorizontalPadding | 16dp | All screen content padding |
| SectionSpacing | 16dp | Between settings/totals sections |
| ItemSpacing | 8dp | LazyColumn spacedBy, spacers |
| CardBorderWidth | 4dp | ItemCard left border |
| DialogCornerRadius | 12dp | All dialog cards |
| BarHeight | 14dp | Proportion and coverage bars |
| AmountColumnWidth | 64dp | Amount columns across all screens |
| NavBarHeight | 56dp | Custom sliding navigation bar |
| FabBottomClearance | 80dp | LazyColumn bottom padding for FAB |

## Common Patterns

| Pattern | Value |
|---------|-------|
| Item card elevation | 1dp tonal (via ItemCard) |
| Item card padding | start=16, end=12, top=7, bottom=7 (via ItemCard) |
| Section headers | labelMedium, primary color (all screens) |
| Subdued column headers | onSurface alpha=0.6 |
| Dialog content padding | 24dp (full edit), 16x12dp (quick edit) |
| FAB | SmallFloatingActionButton, shapes.medium, BottomEnd, end=16, bottom=innerPadding+16 |
| Totals panel padding | horizontal=12, vertical=10 |
| Overlay screen padding | h=24-32dp (wider for readability) |
