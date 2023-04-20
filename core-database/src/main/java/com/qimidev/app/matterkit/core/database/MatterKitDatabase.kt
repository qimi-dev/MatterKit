package com.qimidev.app.matterkit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qimidev.app.matterkit.core.database.dao.MatterDeviceDao
import com.qimidev.app.matterkit.core.database.model.MatterDeviceEntity

@Database(
    entities = [
        MatterDeviceEntity::class
    ],
    version = 1
)
abstract class MatterKitDatabase : RoomDatabase() {

    abstract fun matterDeviceDao(): MatterDeviceDao

}