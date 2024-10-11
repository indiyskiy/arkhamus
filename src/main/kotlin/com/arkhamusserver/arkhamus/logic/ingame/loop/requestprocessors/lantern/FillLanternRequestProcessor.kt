package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.lantern

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.lantern.FillLanternRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLanternRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FillLanternRequestProcessor(
    private val inventoryHandler: InventoryHandler,
    private val redisLanternRepository: RedisLanternRepository
) : NettyRequestProcessor {

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
        if (gameData.canFill) {
            val lantern = gameData.lantern
            if (lantern != null) {
                lantern.fuel = 100.0
                lantern.lanternState = LanternState.FILLED
                redisLanternRepository.save(lantern)
                inventoryHandler.consumeItem(gameData.gameUser!!, Item.SOLARITE)
                gameData.successfullyFilled = true
            }
        }
    }
}