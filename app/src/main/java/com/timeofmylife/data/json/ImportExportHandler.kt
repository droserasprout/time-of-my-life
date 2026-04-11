package com.timeofmylife.data.json

import android.content.Context
import android.net.Uri
import kotlinx.serialization.json.Json

private val json =
    Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

fun writeExportToUri(
    context: Context,
    uri: Uri,
    data: AppDataExport,
) {
    context.contentResolver.openOutputStream(uri)?.use { stream ->
        stream.write(json.encodeToString(AppDataExport.serializer(), data).toByteArray())
    }
}

fun readImportFromUri(
    context: Context,
    uri: Uri,
): AppDataExport {
    val text =
        context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader().readText()
        } ?: error("Cannot read from URI: $uri")
    return json.decodeFromString(AppDataExport.serializer(), text)
}
