package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PretendCultistAbilityProcessor() : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(PretendCultistAbilityProcessor::class.java)
    }

    override fun accepts(castAbility: RedisAbilityCast): Boolean {
        return castAbility.abilityId.toAbility()?.let { ability ->
            ability == Ability.PRETEND_CULTIST
        } == true
    }

    override fun processActive(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: RedisAbilityCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.visibilityModifiers?.remove(VisibilityModifier.PRETEND_CULTIST.name)
    }
}

