package com.qimidev.sdk.matter.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "matter_device"
)
internal data class MatterDeviceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "device_id")
    val deviceId: Long = 0,
    @ColumnInfo(name = "is_paired")
    val isPaired: Boolean = false
)
