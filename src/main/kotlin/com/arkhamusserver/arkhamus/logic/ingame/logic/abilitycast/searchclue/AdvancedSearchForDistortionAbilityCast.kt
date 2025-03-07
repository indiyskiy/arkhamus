package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameDistortionClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameDistortionClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForDistortionAbilityCast(
    private val inGameDistortionClueRepository: InGameDistortionClueRepository,
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(AdvancedSearchForDistortionAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SEARCH_FOR_DISTORTION
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        logger.info("cast $ability")
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        val targetWithGameTags = abilityRequestProcessData.target as? InGameDistortionClue
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
        val distortion = target as? InGameDistortionClue
        if (distortion == null) return false
        castAbility(user, distortion)
        return true
    }

    private fun castAbility(
        user: InGameUser,
        target: InGameDistortionClue,
    ) {
        target.castedAbilityUsers += user.inGameId()
        inGameDistortionClueRepository.save(target)
    }

}