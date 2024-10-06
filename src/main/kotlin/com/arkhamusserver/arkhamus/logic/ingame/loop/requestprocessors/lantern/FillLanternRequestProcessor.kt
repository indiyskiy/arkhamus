package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.lantern

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.lantern.FillLanternRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FillLanternRequestProcessor() : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is FillLanternRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as FillLanternRequestProcessData
        if (gameData.canFillLantern) {
            val lantern = gameData.lantern
            if (lantern != null) {
                lantern.fuel = 100.0
                lantern.filled = true
                gameData.successfullyFilled = true
            }
        }
    }
}