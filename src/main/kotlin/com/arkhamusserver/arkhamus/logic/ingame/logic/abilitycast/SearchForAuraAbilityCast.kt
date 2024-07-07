package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SearchForAuraAbilityCast : AbilityCast {

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
    ) {
        logger.info("cast $ability")
    }

}