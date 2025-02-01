package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameOmenClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameOmenClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForOmenAbilityCast(
    private val inGameOmenClueRepository: InGameOmenClueRepository
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(AdvancedSearchForOmenAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.ADVANCED_SEARCH_FOR_OMEN
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        logger.info("cast $ability")
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        val targetWithGameTags = abilityRequestProcessData.target as? InGameOmenClue
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
        val omen = target as? InGameOmenClue
        if (omen == null) return false
        castAbility(user, omen)
        return true
    }

    private fun castAbility(
        user: InGameUser,
        target: InGameOmenClue,
    ) {
        target.castedAbilityUsers += user.inGameId()
        inGameOmenClueRepository.save(target)
    }
}