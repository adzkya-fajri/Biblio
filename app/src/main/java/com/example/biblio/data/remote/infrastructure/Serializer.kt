package com.example.biblio.infrastructure

import com.example.biblio.data.remote.infrastructure.BigDecimalAdapter
import com.example.biblio.data.remote.infrastructure.BigIntegerAdapter
import com.example.biblio.data.remote.infrastructure.ByteArrayAdapter
import com.example.biblio.data.remote.infrastructure.LocalDateAdapter
import com.example.biblio.data.remote.infrastructure.LocalDateTimeAdapter
import com.example.biblio.data.remote.infrastructure.OffsetDateTimeAdapter
import com.example.biblio.data.remote.infrastructure.URIAdapter
import com.example.biblio.data.remote.infrastructure.UUIDAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object Serializer {
    @JvmStatic
    val moshiBuilder: Moshi.Builder = Moshi.Builder()
        .add(OffsetDateTimeAdapter())
        .add(LocalDateTimeAdapter())
        .add(LocalDateAdapter())
        .add(UUIDAdapter())
        .add(ByteArrayAdapter())
        .add(URIAdapter())
        .add(KotlinJsonAdapterFactory())
        .add(BigDecimalAdapter())
        .add(BigIntegerAdapter())

    @JvmStatic
    val moshi: Moshi by lazy {
        moshiBuilder.build()
    }
}
