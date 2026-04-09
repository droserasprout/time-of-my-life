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
            HelpText("Your assets grouped by reliability:")
            BulletItem("High", "Bank accounts", HighColor)
            BulletItem("Medium", "Investments", MediumColor)
            BulletItem("Low", "Crypto, volatile assets", LowColor)
            HelpText("Tap to quick-edit amount. \"Edit\" for full editing or delete.")
            Spacer(Modifier.height(16.dp))

            SectionTitle("Budget")
            HelpText("Monthly expenses and income with three estimates:")
            BulletItem("Best", "Optimistic", BestBlue)
            BulletItem("Last", "Actual last month", LastGrey)
            BulletItem("Worst", "Pessimistic", WorstOrange)
            HelpText("Tap an amount to quick-edit it. Tap item name to edit or delete. Sort by a-z, avg, or last; tap again to reverse.")
            Spacer(Modifier.height(16.dp))

            SectionTitle("Life Time")
            HelpText("Six scenarios: three reliability tiers crossed with best/worst budget. Balance projections at 1/3/6/12 months, survival time, and final day.")
            HelpText("Filter with the top selector:")
            BulletItem("all", "Expenses and income", MaterialTheme.colorScheme.onSurface)
            BulletItem("expense", "Expenses only", ExpenseRed)
            BulletItem("income", "Income only", IncomeGreen)
            HelpText("Coverage bar at the bottom shows years covered, colored by reliability.")
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
