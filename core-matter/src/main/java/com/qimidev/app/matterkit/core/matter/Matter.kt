package com.qimidev.app.matterkit.core.matter

import com.qimidev.app.matterkit.core.model.MatterSetupPayload

interface Matter {

    fun parseSetupPayload(payloadContent: String): Result<MatterSetupPayload>

    fun parseManualSetupPayload(payloadContent: String): Result<MatterSetupPayload>

}