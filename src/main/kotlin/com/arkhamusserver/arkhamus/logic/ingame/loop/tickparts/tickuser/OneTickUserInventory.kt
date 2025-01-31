package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.tickuser

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.OneTickUser.Companion.POTATO_MADNESS_TICK_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import org.springframework.stereotype.Component

@Component
class OneTickUserInventory(
    private val madnessHandler: UserMadnessHandler,
) {
    fun processInventory(
        user: InGameGameUser,
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

    private fun processItem(
        user: InGameGameUser,
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
            INNOVATE_SOUND_INVESTIGATION_ITEM -> processInnovateSoundInvestigationItem(user)
            else -> {}
        }
    }

    private fun processNoItem(
        user: InGameGameUser,
        item: Item,
    ) {
        when (item) {
            INNOVATE_SCENT_INVESTIGATION_ITEM -> noInnovateScentInvestigationItem(user)
            INNOVATE_SOUND_INVESTIGATION_ITEM -> noInnovateSoundInvestigationItem(user)
            else -> {}
        }
    }

    private fun processInnovateScentInvestigationItem(
        user: InGameGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_SCENT
        user.stateTags += UserStateTag.INVESTIGATING_SCENT
    }

    private fun noInnovateScentInvestigationItem(
        user: InGameGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_SCENT
        user.stateTags -= UserStateTag.INVESTIGATING_SCENT
    }

    private fun processInnovateSoundInvestigationItem(
        user: InGameGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_SOUND
        user.stateTags += UserStateTag.INVESTIGATING_SOUND
    }

    private fun noInnovateSoundInvestigationItem(
        user: InGameGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_SOUND
        user.stateTags -= UserStateTag.INVESTIGATING_SOUND
    }

    private fun processCursedPotato(
        user: InGameGameUser,
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