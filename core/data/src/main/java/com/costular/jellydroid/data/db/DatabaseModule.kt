package com.costular.jellydroid.data.db

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface DatabaseModule {
    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext androidContext: Context,
        ): JellydroidDatabase = Room.databaseBuilder(
            context = androidContext,
            klass = JellydroidDatabase::class.java,
            name = "jellydroid-database"
        ).build()
    }
}