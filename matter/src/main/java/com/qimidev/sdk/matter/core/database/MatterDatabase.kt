package com.qimidev.sdk.matter.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qimidev.sdk.matter.core.database.dao.MatterDeviceDao
import com.qimidev.sdk.matter.core.database.model.MatterDeviceEntity

@Database(
    entities = [
        MatterDeviceEntity::class
    ],
    version = 1
)
internal abstract class MatterDatabase : RoomDatabase() {

    abstract fun matterDeviceDao(): MatterDeviceDao

}