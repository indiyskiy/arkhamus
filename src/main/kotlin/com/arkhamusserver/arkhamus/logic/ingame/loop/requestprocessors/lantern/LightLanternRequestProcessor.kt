package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.lantern

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.lantern.LightLanternRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class LightLanternRequestProcessor() : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is LightLanternRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as LightLanternRequestProcessData
        if (gameData.canLight) {
            val lantern = gameData.lantern
            if (lantern != null) {
                lantern.fuel = 100.0
                lantern.lanternState = LanternState.FILLED
                gameData.successfullyLit = true
            }
        }
    }
}