package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult.ShortTimeEventScentData
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ScentClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForScentAbilityCast(
    private val scentClueHandler: ScentClueHandler,
    private val shortTimeEventHandler: ShortTimeEventHandler
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(AdvancedSearchForScentAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.ADVANCED_SEARCH_FOR_SCENT
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        logger.info("cast $ability")
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        val targetWithGameTags = abilityRequestProcessData.target as? WithTrueIngameId
        if (targetWithGameTags == null) return false
        castAbility(user, targetWithGameTags, globalGameData)
        return true
    }

    override fun cast(
        sourceUser: RedisGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = sourceUser
        val targetWithGameTags = target as? WithTrueIngameId
        if (targetWithGameTags == null) return false
        castAbility(user, targetWithGameTags, globalGameData)
        return true
    }

    private fun castAbility(
        user: RedisGameUser,
        target: WithTrueIngameId,
        data: GlobalGameData
    ) {
        val isObjectScentBad = scentClueHandler.isTargetScentBad(target, data)
        shortTimeEventHandler.createShortTimeEvent(
            objectId = target.inGameId(),
            gameId = data.game.inGameId(),
            globalTimer = data.game.globalTimer,
            type = ShortTimeEventType.SCENT_CLUE_CHECK,
            visibilityModifiers = setOf(VisibilityModifier.ALL),
            data = data,
            sourceUserId = user.inGameId(),
            additionalData = ShortTimeEventScentData(isObjectScentBad)
        )
    }
}