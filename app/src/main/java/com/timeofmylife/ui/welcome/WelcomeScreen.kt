package com.timeofmylife.ui.welcome

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.timeofmylife.ui.theme.BestBlue
import com.timeofmylife.ui.theme.HighColor
import com.timeofmylife.ui.theme.WorstOrange

private const val PREFS_NAME = "welcome"
private const val KEY_SEEN = "seen"

fun hasSeenWelcome(context: Context): Boolean = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SEEN, false)

fun markWelcomeSeen(context: Context) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit().putBoolean(KEY_SEEN, true).apply()
}

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onShowHelp: () -> Unit,
) {
    val context = LocalContext.current
    if (hasSeenWelcome(context)) {
        BackHandler(onBack = onGetStarted)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "Time of My Life",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "How long can you live on your savings?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(40.dp))

                ConceptCard(
                    icon = Icons.Default.AccountBalance,
                    title = "Balances",
                    description =
                        "Add your assets and rate their reliability: high (cash, bank accounts), " +
                            "medium (broker accounts), or low (crypto, debts).",
                    accentColor = HighColor,
                )
                Spacer(Modifier.height(16.dp))
                ConceptCard(
                    icon = Icons.Default.AttachMoney,
                    title = "Budget",
                    description = "Enter monthly expenses and income with three estimates: best case, last month actual, and worst case.",
                    accentColor = BestBlue,
                )
                Spacer(Modifier.height(16.dp))
                ConceptCard(
                    icon = Icons.Default.Timeline,
                    title = "Life Time",
                    description = "See how long your money lasts across six scenarios combining asset reliability with budget estimates.",
                    accentColor = WorstOrange,
                )
            }

            OutlinedButton(
                onClick = onShowHelp,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Show help")
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    markWelcomeSeen(context)
                    onGetStarted()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Get started")
            }
        }
    }
}

@Composable
private fun ConceptCard(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: androidx.compose.ui.graphics.Color,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
