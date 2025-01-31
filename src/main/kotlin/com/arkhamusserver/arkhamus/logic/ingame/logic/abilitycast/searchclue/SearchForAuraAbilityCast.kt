package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.ClueAbilityToVisibilityModifierResolver
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SearchForAuraAbilityCast(
    private val resolver: ClueAbilityToVisibilityModifierResolver
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(SearchForAuraAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SEARCH_FOR_AURA
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        user.stateTags += UserStateTag.INVESTIGATING
        val visibilityModifier = resolver.toVisibilityModifier(ability)
        visibilityModifier?.let {
            user.visibilityModifiers += it
            return true
        }
        return false
    }

    override fun cast(
        sourceUser: InGameGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = sourceUser
        user.stateTags += UserStateTag.INVESTIGATING
        val visibilityModifier = resolver.toVisibilityModifier(ability)
        visibilityModifier?.let {
            user.visibilityModifiers += it
            return true
        }
        return false
    }

}