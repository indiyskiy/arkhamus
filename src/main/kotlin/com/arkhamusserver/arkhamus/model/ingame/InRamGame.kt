package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId

//TODO InGameGame just looks to weird, rename properly later
data class InRamGame(
    override var id: String,
    override var gameId: Long,
    var god: God,
    var currentTick: Long = -1,
    var lastTickSaveHeartbeatActivity: Long = 0,
    var globalTimer: Long = 0,
    var serverTimeLastTick: Long = 0,
    var serverTimeCurrentTick: Long = 0,
    var lastTimeSentResponse: Long = 0,
    var gameStart: Long = System.currentTimeMillis(),
    var state: String = GameState.PENDING.name,
    var gameEndReason: String? = null,
) : WithTrueIngameId, InGameEntity {
    override fun inGameId(): Long {
        return gameId
    }
}