package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame

data class AdminGameLevelStatisticDto(
    val averageGameLength: String,
    val averageGameLengthByGameEndReason: List<GameTimeByReasonElementDto>,
    val winRateByClassElementDto: List<WinRateByClassElementDto>,
    val winRateByReasonElementDto: List<WinRateByReasonElementDto>,
    val activitiesStatistic: List<ActivityStatisticDto>
)

data class WinRateByClassElementDto(
    val role: RoleTypeInGame?,
    val value: Double
)

data class WinRateByReasonElementDto(
    val reason: GameEndReason?,
    val value: Double
)

data class GameTimeByReasonElementDto(
    val reason: GameEndReason?,
    val value: String
)

data class ActivityStatisticDto(
    val type: ActivityType,
    val value: String
)