package com.qimidev.app.matterkit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matter_device")
data class MatterDeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: Long
)