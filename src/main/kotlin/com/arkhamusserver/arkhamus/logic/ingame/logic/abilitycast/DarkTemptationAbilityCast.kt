package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InGameTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisQuestGiverRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuestGiver
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithGameTags
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class DarkTemptationAbilityCast(
    private val inGameTagsHandler: InGameTagsHandler,
    private val redisQuestGiverRepository: RedisQuestGiverRepository,
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.DARK_TEMPTATION
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        temptTarget(abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: RedisGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        temptTarget(target)
        return true
    }

    private fun temptTarget(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val target = abilityRequestProcessData.target
        temptTarget(target)
    }

    private fun temptTarget(target: WithStringId?) {
        if (target != null) {
            inGameTagsHandler.addTag(target as WithGameTags, InGameObjectTag.DARK_THOUGHTS)
            if (target is RedisQuestGiver) {
                redisQuestGiverRepository.save(target)
            }
        }
    }

}