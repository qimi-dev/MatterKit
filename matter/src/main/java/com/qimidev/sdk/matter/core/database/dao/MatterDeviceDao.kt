package com.qimidev.sdk.matter.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.qimidev.sdk.matter.core.database.model.MatterDeviceEntity

@Dao
internal interface MatterDeviceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMatterDevice(matterDevice: MatterDeviceEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateMatterDevice(matterDevice: MatterDeviceEntity)

}