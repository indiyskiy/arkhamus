package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId

data class InGameLevelZone(
    override var id: String,
    override var gameId: Long,
    var levelZoneId: Long,
    var zoneType: ZoneType,
) : InGameEntity, WithTrueIngameId {
    override fun inGameId(): Long {
        return levelZoneId
    }
}