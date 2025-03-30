package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.cultist

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FarsightAbilityProcessor : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FarsightAbilityProcessor::class.java)
    }

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.ability == Ability.FARSIGHT
    }

    override fun processActive(
        castAbility: InGameAbilityCast, globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        if (user != null) {
            user.stateTags -= UserStateTag.FARSIGHT
        }
    }

}


