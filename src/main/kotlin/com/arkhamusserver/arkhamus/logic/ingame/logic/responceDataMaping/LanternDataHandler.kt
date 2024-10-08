package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.LanternData
import org.springframework.stereotype.Component

@Component
class LanternDataHandler(
    private val userLocationHandler: UserLocationHandler,
) {
    fun map(
        myUser: RedisGameUser,
        lanterns: Collection<RedisLantern>,
        levelGeometryData: LevelGeometryData
    ): List<LanternData> {
        return lanterns.map { lantern ->
            val response = LanternData(
                lanternId = lantern.inGameId(),
                lanternState = lantern.lanternState,
                objectState = lantern.state,
                lightRange = lantern.lightRange,
            )
            mask(response, lantern, myUser, levelGeometryData)
            response
        }
    }

    private fun mask(
        responseToMask: LanternData,
        door: RedisLantern,
        myUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (!userLocationHandler.userCanSeeTarget(myUser, door, levelGeometryData, false)) {
            responseToMask.lanternState = LanternState.EMPTY
            responseToMask.objectState = MapObjectState.DISABLED
        }
    }

}