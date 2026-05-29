package com.example.biblio.data.local.dao

import androidx.room.*
import com.example.biblio.data.local.entities.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile LIMIT 1")
    suspend fun getProfile(): ProfileEntity?

    @Query("SELECT * FROM profile LIMIT 1")
    fun getProfileFlow(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("DELETE FROM profile")
    suspend fun deleteProfile()
}
