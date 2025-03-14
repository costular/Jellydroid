package com.costular.jellydroid.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "last_used_at") val lastUsedAt: Instant,
    @ColumnInfo(name = "is_setup_completed") val isSetupCompleted: Boolean,
    @ColumnInfo(name = "login_disclaimer") val loginDisclaimer: String?,
)