package com.example.biblio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BukuDatabase(
    val sections: List<Section>
)