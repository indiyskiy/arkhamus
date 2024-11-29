package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.lantern.FillLanternRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLanternRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import org.springframework.stereotype.Component

@Component
class LanternHandler(
    private val inventoryHandler: InventoryHandler,
    private val redisLanternRepository: RedisLanternRepository
) {
    fun lightLantern(lantern: RedisLantern) {
        lantern.lanternState = LanternState.LIT
        redisLanternRepository.save(lantern)
    }

    fun canFill(
        user: RedisGameUser,
        lantern: RedisLantern?
    ): Boolean {
        val canPay = checkIfUserCanPay(user)
        val lanternEmpty = lantern != null &&
                lantern.lanternState == LanternState.EMPTY
        val canFill = canPay && lanternEmpty
        return canFill
    }

    fun fillLantern(
        lantern: RedisLantern,
        gameData: FillLanternRequestProcessData
    ) {
        val user = gameData.gameUser!!
        fillLantern(user, lantern)
    }

    fun fillLantern(
        user: RedisGameUser,
        lantern: RedisLantern,
    ) {
        lantern.fuel = 100.0
        lantern.lanternState = LanternState.FILLED
        redisLanternRepository.save(lantern)
        inventoryHandler.consumeItem(user, Item.SOLARITE)
    }

    private fun checkIfUserCanPay(
        user: RedisGameUser
    ): Boolean {
        val costItem = Item.SOLARITE
        val costValue = 1
        val canPay = inventoryHandler.userHaveItems(
            user = user,
            requiredItem = costItem,
            howManyItems = costValue
        )
        return canPay
    }

    fun canLight(lantern: RedisLantern) =
        lantern.lanternState == LanternState.FILLED

}