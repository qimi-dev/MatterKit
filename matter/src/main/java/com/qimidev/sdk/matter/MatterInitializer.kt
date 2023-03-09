package com.qimidev.sdk.matter

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

class MatterInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Matter.initialize(context.applicationContext as Application)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf()

}