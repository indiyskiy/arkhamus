package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.tickuser

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.OneTickUser.Companion.POTATO_MADNESS_TICK_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CURSED_POTATO
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.INNOVATE_SCENT_INVESTIGATION_ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class OneTickUserInventory(
    private val madnessHandler: UserMadnessHandler,
) {
    fun processInventory(
        user: RedisGameUser,
        timePassedMillis: Long,
        gameTime: Long
    ) {
        Item.values().forEach { item ->
            val itemsInInventory = user.items.filter { it.item == item }.sumOf { it.number }
            if (itemsInInventory > 0) {
                processItem(user, item, itemsInInventory, timePassedMillis, gameTime)
            } else {
                processNoItem(user, item)
            }
        }
    }

    private fun processNoItem(
        user: RedisGameUser,
        item: Item,
    ) {
        when (item) {
            INNOVATE_SCENT_INVESTIGATION_ITEM -> noInnovateScentInvestigationItem(user)
            else -> {}
        }
    }

    private fun processItem(
        user: RedisGameUser,
        item: Item,
        numberOfItems: Int,
        timePassedMillis: Long,
        gameTime: Long
    ) {
        when (item) {
            CURSED_POTATO -> processCursedPotato(
                user,
                numberOfItems,
                timePassedMillis,
                gameTime
            )

            INNOVATE_SCENT_INVESTIGATION_ITEM -> processInnovateScentInvestigationItem(user)

            else -> {}
        }
    }

    private fun processInnovateScentInvestigationItem(
        user: RedisGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_SCENT
        user.stateTags += UserStateTag.INVESTIGATING_SCENT
    }

    private fun noInnovateScentInvestigationItem(
        user: RedisGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_SCENT
        user.stateTags -= UserStateTag.INVESTIGATING_SCENT
    }

    private fun processCursedPotato(
        user: RedisGameUser,
        numberOfItems: Int,
        timePassedMillis: Long,
        gameTime: Long
    ) {
        madnessHandler.applyMadness(
            user,
            POTATO_MADNESS_TICK_MILLIS * numberOfItems * timePassedMillis,
            gameTime,
        )
    }
}