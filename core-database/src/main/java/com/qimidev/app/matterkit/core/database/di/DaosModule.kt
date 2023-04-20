package com.qimidev.app.matterkit.core.database.di

import com.qimidev.app.matterkit.core.database.MatterKitDatabase
import com.qimidev.app.matterkit.core.database.dao.MatterDeviceDao
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    fun providesMatterDeviceDao(
        database: MatterKitDatabase
    ): MatterDeviceDao = database.matterDeviceDao()

}