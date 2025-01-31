package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithGameTags
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
        user: InGameGameUser,
        data: GlobalGameData
    ) {
        withGameTags.gameTags().forEach { tag ->
            when (tag) {
                InGameObjectTag.PEEKABOO_CURSE -> {
                    processPeekabooCurse(user, withGameTags, tag, data)
                }

                InGameObjectTag.DARK_THOUGHTS -> {}
                InGameObjectTag.SCENT -> {}
                InGameObjectTag.SOUND -> {}
            }
        }
    }

    private fun processPeekabooCurse(
        user: InGameGameUser,
        withGameTags: WithGameTags,
        tag: InGameObjectTag,
        data: GlobalGameData
    ) {
        madnessHandler.applyMadness(user, PEEKABOO_CURSE_ITEM_VALUE, data.game.globalTimer)
        inGameTagsHandler.removeTag(withGameTags, tag)
        if (withGameTags is InGameContainer) {
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
        if (withGameTags is InGameCrafter) {
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