package com.costular.jellydroid.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.costular.jellydroid.data.db.converters.InstantTypeConverter

@Database(
    entities = [ServerEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(InstantTypeConverter::class)
abstract class JellydroidDatabase : RoomDatabase() {
    abstract fun getServerDao(): ServerDao
}