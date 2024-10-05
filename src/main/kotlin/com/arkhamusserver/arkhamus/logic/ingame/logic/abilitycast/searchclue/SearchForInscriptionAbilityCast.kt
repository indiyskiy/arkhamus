package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ClueAbilityToVisibilityModifierResolver
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SearchForInscriptionAbilityCast(
    private val resolver: ClueAbilityToVisibilityModifierResolver
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(SearchForInscriptionAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SEARCH_FOR_INSCRIPTION
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        logger.info("cast $ability")
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        user.stateTags.add(UserStateTag.INVESTIGATING.name)
        val visibilityModifier = resolver.toVisibilityModifier(ability)
        visibilityModifier?.let {
            val visibilityModifiers = (user.visibilityModifiers() + it).distinct()
            user.rewriteVisibilityModifiers(visibilityModifiers)
            return true
        }
        return false
    }

}