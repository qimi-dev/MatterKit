package com.qimidev.app.matterkit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qimidev.app.matterkit.core.database.model.MatterDeviceEntity

@Dao
interface MatterDeviceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreDevice(device: MatterDeviceEntity): Long

    @Query("SELECT * FROM matter_device WHERE device_id = :deviceId")
    suspend fun getDeviceByDeviceId(deviceId: Long): MatterDeviceEntity?

}
