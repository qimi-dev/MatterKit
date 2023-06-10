package com.qimidev.app.matterkit.core.database.di

import android.content.Context
import androidx.room.Room
import com.qimidev.app.matterkit.core.database.MatterKitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesMatterKitDatabase(
        @ApplicationContext context: Context
    ): MatterKitDatabase = Room.databaseBuilder(
        context,
        MatterKitDatabase::class.java,
        "matter-kit-database"
    ).build()

}
