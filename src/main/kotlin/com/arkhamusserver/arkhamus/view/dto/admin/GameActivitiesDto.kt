package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.logic.admin.NiceColor
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType

data class GameActivitiesDto(
    var gameId: Long,
    var userIds: List<Long>,
    var activityTypes: List<ActivityType>,
    var activities: List<GameActivityDto>,
    var levelId: Long,
    var height: Int,
    var width: Int,
)

data class GameActivityDto(
    var type: ActivityType,
    var color: NiceColor,
    val points: List<PointDto>,
    val polygonPoints: String = points.joinToString(" ") { "${it.pointX},${it.pointY}" },
)