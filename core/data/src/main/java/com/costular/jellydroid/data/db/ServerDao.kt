package com.costular.jellydroid.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers ORDER BY last_used_at DESC, NAME asc")
    fun getAll(): List<ServerEntity>

    @Query("SELECT COUNT(*) FROM servers")
    fun serverCount(): Flow<Int>

    @Query("SELECT * FROM servers ORDER BY last_used_at DESC, NAME asc")
    fun observeAll(): Flow<List<ServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addServer(server: ServerEntity)

    @Query("DELETE FROM servers WHERE id = :id")
    suspend fun deleteServer(id: String)

    @Query("DELETE FROM servers")
    suspend fun deleteAll()

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServer(id: String): ServerEntity?
}