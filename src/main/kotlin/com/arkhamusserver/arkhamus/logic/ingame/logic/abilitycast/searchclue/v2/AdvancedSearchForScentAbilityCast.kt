package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisScentClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.clues.RedisScentClue
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForScentAbilityCast(
    private val redisScentClueRepository: RedisScentClueRepository
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
        val targetWithGameTags = abilityRequestProcessData.target as? RedisScentClue
        if (targetWithGameTags == null) return false
        castAbility(user, targetWithGameTags)
        return true
    }

    override fun cast(
        sourceUser: RedisGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = sourceUser
        val scent = target as? RedisScentClue
        if (scent == null) return false
        castAbility(user, scent)
        return true
    }

    private fun castAbility(
        user: RedisGameUser,
        target: RedisScentClue,
    ) {
        target.castedAbilityUsers += user.inGameId()
        redisScentClueRepository.save(target)
    }
}