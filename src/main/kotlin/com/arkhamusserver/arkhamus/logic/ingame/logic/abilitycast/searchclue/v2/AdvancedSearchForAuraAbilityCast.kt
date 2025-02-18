package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameAuraClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameAuraClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForAuraAbilityCast(
    private val inGameAuraClueRepository: InGameAuraClueRepository
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(AdvancedSearchForAuraAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.ADVANCED_SEARCH_FOR_AURA
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        logger.info("cast $ability")
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        val targetWithGameTags = abilityRequestProcessData.target as? InGameAuraClue
        if (targetWithGameTags == null) return false
        castAbility(user, targetWithGameTags)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = sourceUser
        val aura = target as? InGameAuraClue
        if (aura == null) return false
        castAbility(user, aura)
        return true
    }

    private fun castAbility(
        user: InGameUser,
        target: InGameAuraClue,
    ) {
        target.castedAbilityUsers += user.inGameId()
        inGameAuraClueRepository.save(target)
    }
}