package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.aftershock

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult.ShortTimeEventPersonWithTimeData
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult.UserActivityView
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameUserSkinSetting
import org.springframework.stereotype.Component

@Component
class HaveInclusionCastAftershockHandler(
    private val shortTimeEventHandler: ShortTimeEventHandler
) : CastAftershockHandler {
    override fun accept(ability: Ability): Boolean {
        return true
    }

    override fun processCastAftershocks(
        ability: Ability,
        sourceUser: InGameUser,
        target: WithStringId?,
        data: GlobalGameData
    ) {
        if (sourceUser.stateTags.contains(UserStateTag.HAVE_INCLUSION)) {
            createTriggerInclusion(sourceUser, data)
            return
        }
        if (target != null && target is InGameUser) {
            if (target.stateTags.contains(UserStateTag.HAVE_INCLUSION)) {
                createTriggerInclusion(target, data)
            }
        }
    }

    private fun createTriggerInclusion(
        user: InGameUser,
        data: GlobalGameData
    ) {
        shortTimeEventHandler.createShortTimeEvent(
            objectId = user.inGameId(),
            gameId = data.game.inGameId(),
            globalTimer = data.game.globalTimer,
            type = ShortTimeEventType.INCLUSION_TRIGGERED,
            visibilityModifiers = setOf(VisibilityModifier.ALL),
            data = data,
            sourceUserId = null,
            additionalData = ShortTimeEventPersonWithTimeData(
                user = buildAdditionalData(user),
                currentTime = data.game.globalTimer,
                eventTime = data.game.globalTimer,
            )
        )
        user.stateTags -= UserStateTag.HAVE_INCLUSION
    }

    private fun buildAdditionalData(user: InGameUser): UserActivityView = UserActivityView(
        user.inGameId(),
        user.additionalData.originalSkin.nickName,
        InGameUserSkinSetting(
            user.additionalData.originalSkin.nickName,
            user.additionalData.originalSkin.skinColor
        )
    )

}