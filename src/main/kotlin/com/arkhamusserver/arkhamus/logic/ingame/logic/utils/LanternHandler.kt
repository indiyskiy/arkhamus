package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.lantern.FillLanternRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameLanternRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameLantern
import org.springframework.stereotype.Component

@Component
class LanternHandler(
    private val inventoryHandler: InventoryHandler,
    private val inGameLanternRepository: InGameLanternRepository
) {
    fun lightLantern(lantern: InGameLantern) {
        lantern.lanternState = LanternState.LIT
        inGameLanternRepository.save(lantern)
    }

    fun canFill(
        user: InGameUser,
        lantern: InGameLantern?
    ): Boolean {
        val canPay = checkIfUserCanPay(user)
        val lanternEmpty = lantern != null &&
                lantern.lanternState == LanternState.EMPTY
        val canFill = canPay && lanternEmpty
        return canFill
    }

    fun fillLantern(
        lantern: InGameLantern,
        gameData: FillLanternRequestProcessData
    ) {
        val user = gameData.gameUser!!
        fillLantern(user, lantern)
    }

    fun fillLantern(
        user: InGameUser,
        lantern: InGameLantern,
    ) {
        lantern.fuel = 100.0
        lantern.lanternState = LanternState.FILLED
        inGameLanternRepository.save(lantern)
        inventoryHandler.consumeItem(user, Item.SOLARITE)
    }

    private fun checkIfUserCanPay(
        user: InGameUser
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

    fun canLight(lantern: InGameLantern) =
        lantern.lanternState == LanternState.FILLED

}