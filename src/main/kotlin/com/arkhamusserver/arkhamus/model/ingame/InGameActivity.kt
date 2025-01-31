package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint

//all fields should be val couse it should be immutable.
// if you need to make it mutable - you did something wring
data class InGameActivity(
    override val id: String,
    override val gameId: Long,
    val sourceUserId: Long?,
    val activityType: ActivityType,
    val x: Double,
    val y: Double,
    val z: Double,
    val gameTime: Long,
    val relatedGameObjectType: GameObjectType?,
    val relatedGameObjectId: Long?,
    val relatedEventId: Long?,
) : InGameEntity, WithPoint {
    override fun x(): Double = x
    override fun y(): Double = y
    override fun z(): Double = z
}