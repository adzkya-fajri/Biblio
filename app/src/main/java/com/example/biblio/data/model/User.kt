package com.example.biblio.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isSubscribed: Boolean? = false,
    val subscribedUntil: String? = null,
//    val fontStyle: String = "regular"
)