package com.qimidev.app.matterkit.core.matter.di

import android.content.Context
import com.qimidev.app.matterkit.core.matter.Matter
import com.qimidev.app.matterkit.core.matter.MatterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MatterModule {

    @Provides
    @Singleton
    fun providesMatter(
        @ApplicationContext context: Context
    ): Matter = MatterImpl(context)

}