package com.qimidev.app.matterkit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qimidev.app.matterkit.core.model.MatterDevice

@Entity(tableName = "matter_device")
data class MatterDeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: Long
)

fun MatterDeviceEntity.asExternalModel(): MatterDevice = MatterDevice(deviceId = deviceId)
