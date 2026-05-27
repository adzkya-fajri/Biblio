package com.example.biblio.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.biblio.data.remote.dto.User

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int,
    val name: String?,
    val email: String?,
    val avatar: String?,
    val avatarUrl: String?,
    val isSubscribed: Boolean?
)

fun ProfileEntity.toUser() = User(
    id = id,
    name = name,
    email = email,
    avatar = avatar,
    avatarUrl = avatarUrl,
    isSubscribed = isSubscribed
)

fun User.toEntity() = ProfileEntity(
    id = id ?: 0,
    name = name,
    email = email,
    avatar = avatar,
    avatarUrl = avatarUrl,
    isSubscribed = isSubscribed
)
