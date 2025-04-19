package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PretendCultistAbilityProcessor() : ActiveAbilityProcessor {

    companion object {
        private val logger = LoggingUtils.getLogger<PretendCultistAbilityProcessor>()
    }

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.ability == Ability.PRETEND_CULTIST
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        if (user == null) return
        user.visibilityModifiers -= VisibilityModifier.PRETEND_CULTIST
    }
}

