package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.tickuser

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.OneTickUser.Companion.POTATO_MADNESS_TICK_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class OneTickUserInventory(
    private val madnessHandler: UserMadnessHandler,
) {
    fun processInventory(
        user: InGameUser,
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
        user: InGameUser,
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
            INNOVATE_OMEN_INVESTIGATION_ITEM -> processInnovateOmenInvestigationItem(user)
            INNOVATE_CORRUPTION_INVESTIGATION_ITEM -> processInnovateCorruptionInvestigationItem(user)
            INNOVATE_DISTORTION_INVESTIGATION_ITEM -> processInnovateDistortionInvestigationItem(user)
            else -> {}
        }
    }

    private fun processNoItem(
        user: InGameUser,
        item: Item,
    ) {
        when (item) {
            INNOVATE_SCENT_INVESTIGATION_ITEM -> noInnovateScentInvestigationItem(user)
            INNOVATE_SOUND_INVESTIGATION_ITEM -> noInnovateSoundInvestigationItem(user)
            INNOVATE_OMEN_INVESTIGATION_ITEM -> noInnovateOmenInvestigationItem(user)
            INNOVATE_CORRUPTION_INVESTIGATION_ITEM -> noInnovateCorruptionInvestigationItem(user)
            INNOVATE_DISTORTION_INVESTIGATION_ITEM -> noInnovateDistortionInvestigationItem(user)
            else -> {}
        }
    }

    private fun processInnovateScentInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_SCENT
        user.stateTags += UserStateTag.INVESTIGATING_SCENT
    }

    private fun noInnovateScentInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_SCENT
        user.stateTags -= UserStateTag.INVESTIGATING_SCENT
    }

    private fun processInnovateSoundInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_SOUND
        user.stateTags += UserStateTag.INVESTIGATING_SOUND
    }

    private fun processInnovateOmenInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_OMEN
        user.stateTags += UserStateTag.INVESTIGATING_OMEN
    }

    private fun processInnovateCorruptionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_CORRUPTION
        user.stateTags += UserStateTag.INVESTIGATING_CORRUPTION
    }

    private fun processInnovateDistortionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_DISTORTION
        user.stateTags += UserStateTag.INVESTIGATING_DISTORTION
    }

    private fun noInnovateSoundInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_SOUND
        user.stateTags -= UserStateTag.INVESTIGATING_SOUND
    }

    private fun noInnovateOmenInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_OMEN
        user.stateTags -= UserStateTag.INVESTIGATING_OMEN
    }

    private fun noInnovateCorruptionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_CORRUPTION
        user.stateTags -= UserStateTag.INVESTIGATING_CORRUPTION
    }

    private fun noInnovateDistortionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_DISTORTION
        user.stateTags -= UserStateTag.INVESTIGATING_DISTORTION
    }

    private fun processCursedPotato(
        user: InGameUser,
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