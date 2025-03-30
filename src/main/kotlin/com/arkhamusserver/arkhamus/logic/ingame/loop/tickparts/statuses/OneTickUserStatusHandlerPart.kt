package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.statuses

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatus

interface OneTickUserStatusHandlerPart {
    fun updateStatuses(data: GlobalGameData): List<SimpleStatus>
}

data class SimpleStatus(
    val userId: Long,
    val inGameStatus: InGameUserStatus
)