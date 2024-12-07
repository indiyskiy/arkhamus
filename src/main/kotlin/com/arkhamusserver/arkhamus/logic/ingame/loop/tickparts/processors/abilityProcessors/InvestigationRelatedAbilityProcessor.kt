package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.ClueAbilityToVisibilityModifierResolver
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InvestigationRelatedAbilityProcessor(
    private val resolver: ClueAbilityToVisibilityModifierResolver
) : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InvestigationRelatedAbilityProcessor::class.java)
        val relatedSet = setOf(
            Ability.SEARCH_FOR_INSCRIPTION,
            Ability.SEARCH_FOR_SOUND,
            Ability.SEARCH_FOR_SCENT,
            Ability.SEARCH_FOR_AURA,
            Ability.SEARCH_FOR_CORRUPTION,
            Ability.SEARCH_FOR_OMEN,
            Ability.SEARCH_FOR_DISTORTION,
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
        user?.let {
            val ability = castAbility.abilityId.toAbility()!!
            val visibilityModifier = resolver.toVisibilityModifier(ability)
            visibilityModifier?.name?.let { visibilityModifierNotNull ->
                it.visibilityModifiers -= visibilityModifierNotNull
                val allInvetigatingModifiers = resolver.allStrings()
                if (it.visibilityModifiers.none {
                        it in allInvetigatingModifiers
                    }
                ) {
                    it.stateTags -= UserStateTag.INVESTIGATING
                }
            }
        }
    }
}

