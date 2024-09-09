package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CreateCastAbilityEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import org.springframework.stereotype.Component

@Component
class SeeTheOmenAbilityCast(
    private val createCastAbilityEventHandler: CreateCastAbilityEventHandler
) : AbilityCast {

    companion object {
        private val abilitiesList = listOf(
            Ability.SEARCH_FOR_INSCRIPTION,
            Ability.SEARCH_FOR_SOUND,
            Ability.SEARCH_FOR_SCENT,
            Ability.SEARCH_FOR_AURA,
            Ability.SEARCH_FOR_CORRUPTION,
            Ability.SEARCH_FOR_OMEN,
            Ability.SEARCH_FOR_DISTORTION,
        )
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SEE_THE_OMEN
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean{
        seeTheOmen(globalGameData, abilityRequestProcessData)
        return true
    }

    private fun seeTheOmen(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        currentUser?.let { currentUserNotNull ->
            currentUser.stateTags.add(UserStateTag.INVESTIGATING.name)
            abilitiesList.forEach {
                createCastAbilityEventHandler.createCastAbilityEvent(
                    it,
                    currentUserNotNull.userId,
                    globalGameData.game.gameId!!,
                    globalGameData.game.globalTimer
                )
            }
        }
    }
}