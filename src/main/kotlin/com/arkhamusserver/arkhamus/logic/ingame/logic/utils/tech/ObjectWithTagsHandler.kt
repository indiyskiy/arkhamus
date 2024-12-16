package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
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
        madnessHandler.applyMadness(user, PEEKABOO_CURSE_ITEM_VALUE, data.game.globalTimer)
        inGameTagsHandler.removeTag(withGameTags, tag)
        if (withGameTags is RedisContainer) {
            shortTimeEventHandler.createShortTimeEvent(
                objectId = withGameTags.inGameId(),
                gameId = data.game.gameId!!,
                globalTimer = data.game.globalTimer,
                type = ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CONTAINER,
                visibilityModifiers = setOf(VisibilityModifier.ALL),
                data = data,
                sourceUserId = user.userId
            )
        }
        if (withGameTags is RedisCrafter) {
            shortTimeEventHandler.createShortTimeEvent(
                objectId = withGameTags.inGameId(),
                gameId = data.game.gameId!!,
                globalTimer = data.game.globalTimer,
                type = ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CRAFTER,
                visibilityModifiers = setOf(VisibilityModifier.ALL),
                data = data,
                sourceUserId = user.userId
            )
        }
    }
}