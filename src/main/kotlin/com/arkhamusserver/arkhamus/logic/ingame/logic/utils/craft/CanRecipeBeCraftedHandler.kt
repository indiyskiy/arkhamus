package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.DistortionClueHandler
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CanRecipeBeCraftedHandler(
    private val userInventoryHandler: InventoryHandler,
) {

    companion object {
        private val logger = LoggingUtils.getLogger<CanRecipeBeCraftedHandler>()
    }

    fun canUserCraft(
        user: InGameUser,
        recipe: Recipe,
        crafter: InGameCrafter,
        holdCrafterNeeded: Boolean = true
    ): Boolean {
        return haveRequiredItems(recipe, crafter, user) &&
                rightTypeOfCrafter(recipe, crafter) &&
                (!holdCrafterNeeded || crafterHoldByMe(user, crafter))
    }

    private fun crafterHoldByMe(user: InGameUser, crafter: InGameCrafter) =
        (crafter.holdingUser == user.inGameId()).also {
            logger.warn("crafterHoldByMe $this")
        }

    private fun rightTypeOfCrafter(
        recipe: Recipe,
        crafter: InGameCrafter
    ): Boolean =
        (crafter.crafterType in recipe.crafterTypes).also {
            logger.warn("rightTypeOfCrafter $this")
        }

    private fun haveRequiredItems(
        recipe: Recipe,
        crafter: InGameCrafter,
        user: InGameUser
    ): Boolean {
        return recipe.ingredients.all {
            userInventoryHandler.haveRequiredItems(it, crafter, user)
        }.also {
            logger.warn("haveRequiredItems $this")
        }
    }

}