package com.timeofmylife.data.json

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.data.model.Reliability

fun writeCsvToFolder(
    context: Context,
    folderUri: Uri,
    balances: List<Balance>,
    budgetItems: List<BudgetItem>
) {
    val folder = DocumentFile.fromTreeUri(context, folderUri) ?: error("Cannot open folder")

    folder.createFile("text/csv", "balances.csv")?.uri?.let { uri ->
        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { w ->
            w.write("name,reliability,amount")
            w.newLine()
            balances.forEach { b ->
                w.write("${escapeCsv(b.name)},${b.reliability.name},${b.amount}")
                w.newLine()
            }
        }
    }

    folder.createFile("text/csv", "budget_items.csv")?.uri?.let { uri ->
        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { w ->
            w.write("name,type,goodAmount,badAmount,lastAmount")
            w.newLine()
            budgetItems.forEach { b ->
                w.write("${escapeCsv(b.name)},${b.type.name},${b.goodAmount},${b.badAmount},${b.lastAmount}")
                w.newLine()
            }
        }
    }
}

fun readCsvFromFolder(
    context: Context,
    folderUri: Uri
): Pair<List<Balance>, List<BudgetItem>> {
    val folder = DocumentFile.fromTreeUri(context, folderUri) ?: error("Cannot open folder")

    val balances = folder.findFile("balances.csv")?.uri?.let { uri ->
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { r ->
            r.readLines().drop(1).filter { it.isNotBlank() }.map { line ->
                val cols = parseCsvLine(line)
                Balance(
                    name = cols[0],
                    reliability = Reliability.valueOf(cols[1]),
                    amount = cols[2].toDouble()
                )
            }
        }
    } ?: emptyList()

    val budgetItems = folder.findFile("budget_items.csv")?.uri?.let { uri ->
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { r ->
            r.readLines().drop(1).filter { it.isNotBlank() }.map { line ->
                val cols = parseCsvLine(line)
                BudgetItem(
                    name = cols[0],
                    type = ItemType.valueOf(cols[1]),
                    goodAmount = cols[2].toDouble(),
                    badAmount = cols[3].toDouble(),
                    lastAmount = cols.getOrElse(4) { "0.0" }.toDouble()
                )
            }
        }
    } ?: emptyList()

    return balances to budgetItems
}

private fun escapeCsv(value: String): String =
    if (value.contains(',') || value.contains('"') || value.contains('\n'))
        "\"${value.replace("\"", "\"\"")}\""
    else value

private fun parseCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    var i = 0
    while (i < line.length) {
        if (line[i] == '"') {
            val sb = StringBuilder()
            i++ // skip opening quote
            while (i < line.length) {
                if (line[i] == '"') {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        sb.append('"')
                        i += 2
                    } else {
                        i++ // skip closing quote
                        break
                    }
                } else {
                    sb.append(line[i])
                    i++
                }
            }
            result.add(sb.toString())
            if (i < line.length && line[i] == ',') i++ // skip comma
        } else {
            val next = line.indexOf(',', i)
            if (next == -1) {
                result.add(line.substring(i))
                break
            } else {
                result.add(line.substring(i, next))
                i = next + 1
            }
        }
    }
    return result
}
