package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CanRecipeBeCraftedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CraftProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class CraftMadnessLogic(
    private val inventoryHandler: InventoryHandler,
    private val userLocationHandler: UserLocationHandler,
    private val recipesSource: RecipesSource,
    private val canRecipeBeCraftedHandler: CanRecipeBeCraftedHandler,
    private val craftProcessHandler: CraftProcessHandler,
    private val crafterRepository: RedisCrafterRepository
) {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun craftSomething(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val craftersNearby = openCrafters(data, user)
        if (craftersNearby.isNotEmpty()) {
            val crafter = craftersNearby.random(random)
            val recipeCanBeCraftedList = recipesSource.getAllRecipes().filter {
                canRecipeBeCraftedHandler.canUserCraft(user, it, crafter, false)
            }
            if (recipeCanBeCraftedList.isNotEmpty()) {
                val recipe = recipeCanBeCraftedList.random(random)
                inventoryHandler.consumeItems(recipe, user, crafter)
                craftProcessHandler.startCraftProcess(
                    recipe,
                    crafter,
                    user.inGameId(),
                    data.game.inGameId(),
                    data.game.globalTimer
                )
                crafterRepository.save(crafter)
                return true
            }
        }
        return false
    }

    private fun openCrafters(
        data: GlobalGameData,
        user: RedisGameUser
    ): List<RedisCrafter> = data.crafters.values.filter {
        userLocationHandler.userCanSeeTarget(
            user,
            it,
            data.levelGeometryData,
            true
        ) && it.state == MapObjectState.ACTIVE
    }.toList()
}