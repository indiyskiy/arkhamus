package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.ClueAbilityToVisibilityModifierResolver
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.collections.get
import kotlin.collections.minus

@Component
class InvestigationRelatedAbilityProcessor(
    private val resolver: ClueAbilityToVisibilityModifierResolver
) : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InvestigationRelatedAbilityProcessor::class.java)
        val relatedSet = setOf(
            Ability.SEARCH_FOR_INSCRIPTION,
            Ability.SEARCH_FOR_AURA,
            Ability.SEARCH_FOR_CORRUPTION,
            Ability.SEARCH_FOR_DISTORTION,
        )
    }

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.abilityId.toAbility()?.let { ability ->
            ability in relatedSet
        } == true
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            val ability = castAbility.abilityId.toAbility()!!
            val visibilityModifier = resolver.toVisibilityModifier(ability)
            visibilityModifier?.let { visibilityModifierNotNull ->
                it.visibilityModifiers -= visibilityModifierNotNull
                val allInvetigatingModifiers = resolver.all()
                if (it.visibilityModifiers.none {
                        it in allInvetigatingModifiers
                    }
                ) {
                    it.stateTags - UserStateTag.INVESTIGATING
                }
            }
        }
    }
}