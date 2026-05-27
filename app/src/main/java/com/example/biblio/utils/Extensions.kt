package com.example.biblio.utils

import com.example.biblio.BuildConfig

fun String?.toAbsoluteUrl(): String {
    if (this.isNullOrBlank()) return ""
    if (this.startsWith("http")) return this

    val baseUrl = BuildConfig.CDN_URL.removeSuffix("/")

    return "$baseUrl/${this.removePrefix("/")}"
}
