package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FarsightAbilityProcessor : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FarsightAbilityProcessor::class.java)
        val relatedSet = setOf(
            Ability.FARSIGHT,
        )
    }

    override fun accepts(castAbility: RedisAbilityCast): Boolean {
        return castAbility.abilityId.toAbility()?.let { ability ->
            ability in relatedSet
        } == true
    }

    override fun processActive(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: RedisAbilityCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.stateTags?.remove(UserStateTag.FARSIGHT.name)
    }

    private fun Int.toAbility(): Ability? =
        Ability.values().firstOrNull { it.id == this }

}


