package com.qimidev.app.matterkit.core.data.repository

import com.qimidev.app.matterkit.core.database.dao.MatterDeviceDao
import javax.inject.Inject

class CacheableMatterDeviceRepository @Inject constructor(
    private val matterDeviceDao: MatterDeviceDao
) : MatterDeviceRepository {



}