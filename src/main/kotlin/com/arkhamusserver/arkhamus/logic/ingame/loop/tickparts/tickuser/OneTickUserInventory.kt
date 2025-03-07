package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.tickuser

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
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
        data: GlobalGameData,
        timePassedMillis: Long,
        gameTime: Long
    ) {
        Item.values().forEach { item ->
            val itemsInInventory = user.items.filter { it.item == item }.sumOf { it.number }
            if (itemsInInventory > 0) {
                processItem(user, item, itemsInInventory, timePassedMillis, gameTime, data)
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
        gameTime: Long,
        globalGameData: GlobalGameData
    ) {
        when (item) {
            CURSED_POTATO -> processCursedPotato(
                user,
                numberOfItems,
                timePassedMillis,
                gameTime,
                globalGameData
            )

            SCENT_INVESTIGATION_ITEM -> processScentInvestigationItem(user)
            SOUND_INVESTIGATION_ITEM -> processSoundInvestigationItem(user)
            OMEN_INVESTIGATION_ITEM -> processOmenInvestigationItem(user)
            CORRUPTION_INVESTIGATION_ITEM -> processCorruptionInvestigationItem(user)
            DISTORTION_INVESTIGATION_ITEM -> processDistortionInvestigationItem(user)
            AURA_INVESTIGATION_ITEM -> processAuraInvestigationItem(user)
            INSCRIPTION_INVESTIGATION_ITEM -> processInscriptionInvestigationItem(user)
            else -> {}
        }
    }

    private fun processNoItem(
        user: InGameUser,
        item: Item,
    ) {
        when (item) {
            SCENT_INVESTIGATION_ITEM -> noScentInvestigationItem(user)
            SOUND_INVESTIGATION_ITEM -> noSoundInvestigationItem(user)
            OMEN_INVESTIGATION_ITEM -> noOmenInvestigationItem(user)
            CORRUPTION_INVESTIGATION_ITEM -> noCorruptionInvestigationItem(user)
            DISTORTION_INVESTIGATION_ITEM -> noDistortionInvestigationItem(user)
            AURA_INVESTIGATION_ITEM -> noAuraInvestigationItem(user)
            INSCRIPTION_INVESTIGATION_ITEM -> noInscriptionInvestigationItem(user)
            else -> {}
        }
    }

    private fun processScentInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_SCENT
        user.stateTags += UserStateTag.INVESTIGATING_SCENT
    }

    private fun noScentInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_SCENT
        user.stateTags -= UserStateTag.INVESTIGATING_SCENT
    }

    private fun processSoundInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_SOUND
        user.stateTags += UserStateTag.INVESTIGATING_SOUND
    }

    private fun processOmenInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_OMEN
        user.stateTags += UserStateTag.INVESTIGATING_OMEN
    }

    private fun processCorruptionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_CORRUPTION
        user.stateTags += UserStateTag.INVESTIGATING_CORRUPTION
    }

    private fun processDistortionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_DISTORTION
        user.stateTags += UserStateTag.INVESTIGATING_DISTORTION
    }

    private fun processAuraInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_AURA
        user.stateTags += UserStateTag.INVESTIGATING_AURA
    }

    private fun processInscriptionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers += VisibilityModifier.HAVE_ITEM_INSCRIPTION
        user.stateTags += UserStateTag.INVESTIGATING_INSCRIPTION
    }

    private fun noSoundInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_SOUND
        user.stateTags -= UserStateTag.INVESTIGATING_SOUND
    }

    private fun noOmenInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_OMEN
        user.stateTags -= UserStateTag.INVESTIGATING_OMEN
    }

    private fun noCorruptionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_CORRUPTION
        user.stateTags -= UserStateTag.INVESTIGATING_CORRUPTION
    }

    private fun noDistortionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_DISTORTION
        user.stateTags -= UserStateTag.INVESTIGATING_DISTORTION
    }

    private fun noAuraInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_AURA
        user.stateTags -= UserStateTag.INVESTIGATING_AURA
    }

    private fun noInscriptionInvestigationItem(
        user: InGameUser,
    ) {
        user.visibilityModifiers = user.visibilityModifiers - VisibilityModifier.HAVE_ITEM_INSCRIPTION
        user.stateTags -= UserStateTag.INVESTIGATING_INSCRIPTION
    }

    private fun processCursedPotato(
        user: InGameUser,
        numberOfItems: Int,
        timePassedMillis: Long,
        gameTime: Long,
        globalGameData: GlobalGameData
    ) {
        madnessHandler.tryApplyMadness(
            user,
            POTATO_MADNESS_TICK_MILLIS * numberOfItems * timePassedMillis,
            gameTime,
            globalGameData
        )
    }
}