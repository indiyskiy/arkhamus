package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameCorruptionClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameCorruptionClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForCorruptionAbilityCast(
    private val inGameCorruptionClueRepository: InGameCorruptionClueRepository,
    private val eventHandler: TimeEventHandler
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(AdvancedSearchForCorruptionAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SEARCH_FOR_CORRUPTION
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        logger.info("cast $ability")
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        val targetWithGameTags = abilityRequestProcessData.target as? InGameCorruptionClue
        if (targetWithGameTags == null) return false
        castAbility(user, targetWithGameTags, globalGameData)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = sourceUser
        val corruption = target as? InGameCorruptionClue
        if (corruption == null) return false
        castAbility(user, corruption, globalGameData)
        return true
    }

    private fun castAbility(
        user: InGameUser,
        target: InGameCorruptionClue,
        globalGameData: GlobalGameData
    ) {
        val event = globalGameData.timeEvents.firstOrNull {
            it.state == InGameTimeEventState.ACTIVE &&
                    it.type == InGameTimeEventType.CORRUPTION_CLUE_GROWTH &&
                    it.targetObjectId == target.inGameId()
        }
        if(event == null) {
            createGrowthEvent(user, target, globalGameData)
        }

        target.castedAbilityUsers += user.inGameId()
        inGameCorruptionClueRepository.save(target)
    }

    private fun createGrowthEvent(
        user: InGameUser,
        clue: InGameCorruptionClue,
        data: GlobalGameData
    ) {
        eventHandler.createEvent(
            data.game,
            InGameTimeEventType.CORRUPTION_CLUE_GROWTH,
            user,
            clue,
            timeLeft = clue.totalTimeUntilNullify
        )
    }
}