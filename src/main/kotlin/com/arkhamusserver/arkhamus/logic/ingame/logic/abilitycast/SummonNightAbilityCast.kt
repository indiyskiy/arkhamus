package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class SummonNightAbilityCast(
    private val timeEventHandler: TimeEventHandler
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SUMMON_NIGHT
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        createSummonedNightEvent(
            globalGameData.game,
            abilityRequestProcessData.gameUser!!
        )
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        createSummonedNightEvent(
            globalGameData.game,
            sourceUser
        )
        return true
    }

    private fun createSummonedNightEvent(
        game: InRamGame,
        sourceUser: InGameUser
    ) {
        timeEventHandler.createEvent(
            game,
            InGameTimeEventType.SUMMONED_NIGHT,
            sourceUser
        )
    }
}