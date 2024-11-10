package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
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
    companion object {
        const val PEEKABOO_CURSE_ITEM_VALUE = 20.0
    }

    fun processObject(
        withGameTags: WithGameTags,
        user: RedisGameUser,
        data: GlobalGameData
    ) {
        withGameTags.gameTags().forEach { tag ->
            when (tag) {
                InGameObjectTag.PEEKABOO_CURSE.name -> {
                    processPeekabooCurse(user, withGameTags, tag, data)
                }
            }
        }
    }

    private fun processPeekabooCurse(
        user: RedisGameUser,
        withGameTags: WithGameTags,
        tag: String,
        data: GlobalGameData
    ) {
        madnessHandler.applyMadness(user, PEEKABOO_CURSE_ITEM_VALUE)
        inGameTagsHandler.removeTag(withGameTags, tag)
        if (withGameTags is RedisContainer) {
            shortTimeEventHandler.createShortTimeEvent(
                withGameTags.inGameId(),
                data.game.gameId!!,
                data.game.globalTimer,
                ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CONTAINER,
                setOf(VisibilityModifier.ALL.name),
                data
            )
        }
        if (withGameTags is RedisCrafter) {
            shortTimeEventHandler.createShortTimeEvent(
                withGameTags.inGameId(),
                data.game.gameId!!,
                data.game.globalTimer,
                ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CRAFTER,
                setOf(VisibilityModifier.ALL.name),
                data
            )
        }
    }
}