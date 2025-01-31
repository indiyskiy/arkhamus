package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult.ShortTimeEventPersonWithTimeData
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult.UserActivityView
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameActivityRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameUserSkinSetting
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LastPersonTouchAbilityCast(
    private val activityRepository: InGameActivityRepository,
    private val shortTimeEventHandler: ShortTimeEventHandler
) : AbilityCast {

    companion object {
        private val relatedTargetTypes = setOf(GameObjectType.CONTAINER, GameObjectType.CRAFTER)
        private val relatedActivityTypes = setOf(
            ActivityType.CONTAINER_OPENED,
            ActivityType.CONTAINER_CLOSED,
            ActivityType.CRAFTER_OPENED,
            ActivityType.CRAFTER_CLOSED,
        )
        private val logger = LoggerFactory.getLogger(LastPersonTouchAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.TAKE_FINGERPRINTS
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        val target = abilityRequestProcessData.target
        if (target == null || target !is WithTrueIngameId) return false
        val result = whoTouchedLast(target, globalGameData)
        if (result != null) {
            createShortTimeEvent(
                abilityRequestProcessData.gameUser,
                target,
                result,
                globalGameData,
            )
        } else {
            logger.info("No one touched, so no events")
        }
        return true
    }

    override fun cast(
        sourceUser: InGameGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null || target !is WithTrueIngameId) return false
        val result = whoTouchedLast(target, globalGameData)
        if (result != null) {
            createShortTimeEvent(sourceUser, target, result, globalGameData)
        } else {
            logger.info("No one touched, so no events")
        }
        return true
    }

    fun createShortTimeEvent(
        sourceUser: InGameGameUser?,
        target: WithTrueIngameId,
        result: ShortTimeEventPersonWithTimeData,
        globalGameData: GlobalGameData,
    ) {
        logger.info("create who-touched short time event")
        val type = if (target is InGameCrafter) {
            ShortTimeEventType.LAST_PERSON_TOUCH_CRAFTER
        } else {
            ShortTimeEventType.LAST_PERSON_TOUCH_CONTAINER
        }
        shortTimeEventHandler.createShortTimeEvent(
            objectId = target.inGameId(),
            gameId = globalGameData.game.inGameId(),
            globalTimer = globalGameData.game.globalTimer,
            type = type,
            visibilityModifiers = setOf(VisibilityModifier.ALL),
            data = globalGameData,
            additionalData = result,
            sourceUserId = sourceUser?.inGameId(),
        )
    }

    private fun whoTouchedLast(
        target: WithTrueIngameId,
        data: GlobalGameData
    ): ShortTimeEventPersonWithTimeData? {
        val activity = activityRepository
            .findByGameId(data.game.inGameId())
            .filter {
                it.activityType in relatedActivityTypes &&
                        it.relatedGameObjectType in relatedTargetTypes &&
                        it.relatedGameObjectId == target.inGameId()
            }.maxByOrNull { it.gameTime }
        val whoTouched = activity?.let {
            val user = it.sourceUserId?.let {
                data.users[it]
            }
            val time = it.gameTime
            ShortTimeEventPersonWithTimeData(
                user = user?.let { userNotNull ->
                    UserActivityView(
                        id = userNotNull.inGameId(),
                        nickName = userNotNull.nickName,
                        skin = InGameUserSkinSetting(userNotNull.originalSkin)
                    )
                },
                currentTime = data.game.globalTimer,
                eventTime = time,
            )
        }
        if (whoTouched == null) {
            logger.info("No one touched")
        } else {
            logger.info("Last person touched: $whoTouched")
        }
        return whoTouched
    }

}