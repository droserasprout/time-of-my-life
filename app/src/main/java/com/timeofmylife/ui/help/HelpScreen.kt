package com.timeofmylife.ui.help

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timeofmylife.ui.theme.*

@Composable
fun HelpScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text("Help", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            SectionTitle("Balances")
            HelpText("Track your savings across multiple accounts. Each balance has a name, amount, and reliability level:")
            BulletItem("High", "Reliable assets like bank accounts", HighColor)
            BulletItem("Medium", "Somewhat predictable like investments", MediumColor)
            BulletItem("Low", "Volatile assets like crypto", LowColor)
            HelpText("Tap a balance to quickly update its amount. Tap \"Edit\" in the popup for full editing or to delete.")
            Spacer(Modifier.height(16.dp))

            SectionTitle("Budget")
            HelpText("Enter your monthly expenses and income. Each item has three estimates:")
            BulletItem("Best", "Optimistic monthly amount", BestBlue)
            BulletItem("Last", "What you actually spent/earned last month", LastGrey)
            BulletItem("Worst", "Pessimistic monthly amount", WorstOrange)
            HelpText("Tap any amount column to quickly update that value. Tap the item name for full editing.")
            HelpText("Use the sort selector at the top to order items alphabetically or by size. Tap the active option again to reverse the order.")
            Spacer(Modifier.height(16.dp))

            SectionTitle("Life Time")
            HelpText("Shows how long your savings will last across six scenarios, combining three reliability tiers with best/worst budget estimates.")
            HelpText("The balance table projects your remaining balance at 1, 3, 6, and 12 months. The survival table shows total time left and the projected final day.")
            HelpText("Use the selector at the top to filter calculations:")
            BulletItem("all", "Both expenses and income", MaterialTheme.colorScheme.onSurface)
            BulletItem("expense", "Expenses only, ignore income", ExpenseRed)
            BulletItem("income", "Income only, ignore expenses", IncomeGreen)
            HelpText("The coverage bar at the bottom visualizes how many years your savings cover under worst and best budget scenarios. Segments are colored by reliability tier.")
            Spacer(Modifier.height(16.dp))

            SectionTitle("Navigation")
            HelpText("Swipe left and right between screens, or tap the bottom navigation bar. The four screens are Balances, Budget, Life Time, and Settings.")
            Spacer(Modifier.height(16.dp))

            SectionTitle("Settings")
            HelpText("Toggle demo mode to hide all amounts. Export and import your data as JSON (all data in one file) or CSV (separate files for balances and budget items).")
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun HelpText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun BulletItem(label: String, description: String, color: androidx.compose.ui.graphics.Color) {
    Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp)) {
        Text("  ", style = MaterialTheme.typography.bodyMedium)
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color)
        Text(" \u2014 $description", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
