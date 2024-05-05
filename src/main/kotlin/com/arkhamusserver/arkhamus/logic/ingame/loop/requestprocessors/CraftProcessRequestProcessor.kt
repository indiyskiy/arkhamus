package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.logic.CraftProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.CraftProcessRequestProcessData
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import org.springframework.stereotype.Component

@Component
class CraftProcessRequestProcessor(
    private val craftProcessHandler: CraftProcessHandler,
    private val inventoryHandler: InventoryHandler
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.nettyRequestMessage is AbilityRequestMessage
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val craftProcessRequestProcessData = requestDataHolder.requestProcessData as CraftProcessRequestProcessData
        val canBeStarted = craftProcessRequestProcessData.canBeStarted
        if (canBeStarted) {
            consumeItems(
                craftProcessRequestProcessData.recipe!!,
                craftProcessRequestProcessData.gameUser!!,
                craftProcessRequestProcessData.crafter!!
            )
            createCraftProcess(
                craftProcessRequestProcessData,
                requestDataHolder.userAccount.id!!,
                requestDataHolder.gameSession!!.id!!,
                globalGameData.game.globalTimer
            )
            craftProcessRequestProcessData.startedSuccessfully = true
        }
    }

    private fun consumeItems(
        recipe: Recipe,
        gameUser: RedisGameUser,
        crafter: RedisCrafter
    ) {
        inventoryHandler.consumeItems(recipe, gameUser, crafter)
    }

    private fun createCraftProcess(
        craftProcessRequestProcessData: CraftProcessRequestProcessData,
        userId: Long,
        gameId: Long,
        globalTimer: Long
    ) {
        craftProcessHandler.startCraftProcess(
            craftProcessRequestProcessData.recipe!!,
            craftProcessRequestProcessData.crafter!!,
            userId,
            gameId,
            globalTimer
        )
    }
}