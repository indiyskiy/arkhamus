package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithGameTags
import org.springframework.stereotype.Component

@Component
class ObjectWithTagsHandler(
    private val inGameTagsHandler: InGameTagsHandler,
    private val madnessHandler: UserMadnessHandler,
    private val shortTimeEventHandler: ShortTimeEventHandler
) {
    fun processObject(
        withGameTags: WithGameTags,
        user: RedisGameUser,
        data: GlobalGameData
    ) {
        withGameTags.gameTags().forEach { tag ->
            when (tag) {
                InGameObjectTag.PEEKABOO_CURSE -> {
                    processPeekabooCurse(user, withGameTags, tag, data)
                }
            }
        }
    }

    private fun processPeekabooCurse(
        user: RedisGameUser,
        withGameTags: WithGameTags,
        tag: InGameObjectTag,
        data: GlobalGameData
    ) {
        madnessHandler.applyMadness(user, 10)
        inGameTagsHandler.removeTag(withGameTags, tag)
        if (withGameTags is RedisContainer) {
            shortTimeEventHandler.createShortTimeEvent(
                withGameTags.inGameId(),
                data.game.gameId!!,
                data.game.globalTimer,
                ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CONTAINER,
                Ability.PEEKABOO_CURSE_ITEM.visibilityModifiers
            )
        }
        if (withGameTags is RedisCrafter) {
            shortTimeEventHandler.createShortTimeEvent(
                withGameTags.inGameId(),
                data.game.gameId!!,
                data.game.globalTimer,
                ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CRAFTER,
                Ability.PEEKABOO_CURSE_ITEM.visibilityModifiers
            )
        }
    }
}