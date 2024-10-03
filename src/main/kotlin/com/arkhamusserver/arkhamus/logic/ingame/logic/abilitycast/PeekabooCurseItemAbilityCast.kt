package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InGameTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithGameTags
import org.springframework.stereotype.Component

@Component
class PeekabooCurseItemAbilityCast(
    private val inGameTagsHandler: InGameTagsHandler,
    private val redisContainerRepository: RedisContainerRepository,
    private val redisCrafterRepository: RedisCrafterRepository,
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.PEEKABOO_CURSE_ITEM
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        curseItem(abilityRequestProcessData)
        return true
    }

    private fun curseItem(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val target = abilityRequestProcessData.target
        if (target != null) {
            inGameTagsHandler.addTag(target as WithGameTags, InGameObjectTag.PEEKABOO_CURSE)
            if (target is RedisContainer) {
                redisContainerRepository.save(target)
            }
            if (target is RedisCrafter) {
                redisCrafterRepository.save(target)
            }
        }
    }

}