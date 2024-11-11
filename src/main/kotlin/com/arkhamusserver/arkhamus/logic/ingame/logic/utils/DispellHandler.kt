package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisQuestGiverRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisQuestGiver
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class DispellHandler(
    private val inGameTagsHandler: InGameTagsHandler,
    private val redisContainerRepository: RedisContainerRepository,
    private val redisCrafterRepository: RedisCrafterRepository,
    private val questGiverRepository: RedisQuestGiverRepository,
) {
    fun dispellItem(target: WithStringId?) {
        if (target != null) {
            if (target is RedisContainer) {
                inGameTagsHandler.removeTag(target, InGameObjectTag.PEEKABOO_CURSE)
                redisContainerRepository.save(target)
            }
            if (target is RedisCrafter) {
                inGameTagsHandler.removeTag(target, InGameObjectTag.PEEKABOO_CURSE)
                redisCrafterRepository.save(target)
            }
            if (target is RedisQuestGiver) {
                inGameTagsHandler.removeTag(target, InGameObjectTag.DARK_THOUGHTS)
                questGiverRepository.save(target)
            }
        }
    }
}