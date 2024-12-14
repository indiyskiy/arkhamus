package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult.PersonWithTime
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult.UserActivityView
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisActivityRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.redis.parts.RedisUserSkinSetting
import org.springframework.stereotype.Component

@Component
class LastPersonTouchAbilityCast(
    private val activityRepository: RedisActivityRepository,
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
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.TAKE_FINGERPRINTS
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        val result = whoTouchedLast(abilityRequestProcessData.target, globalGameData)
        if (result == null) return true
        createShortTimeEvent(
            abilityRequestProcessData.target!!,
            result,
            globalGameData,
        )
        return true
    }

    override fun cast(
        sourceUser: RedisGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        val result = whoTouchedLast(target, globalGameData)
        if (result == null) return true
        createShortTimeEvent(target!!, result, globalGameData)
        return true
    }

    fun createShortTimeEvent(
        target: WithStringId,
        result: PersonWithTime,
        globalGameData: GlobalGameData,
    ) {
        shortTimeEventHandler.createShortTimeEvent(
            objectId = target.stringId().toLong(),
            gameId = globalGameData.game.inGameId(),
            globalTimer = globalGameData.game.globalTimer,
            type = if (target is RedisCrafter) {
                ShortTimeEventType.LAST_PERSON_TOUCH_CRAFTER
            } else {
                ShortTimeEventType.LAST_PERSON_TOUCH_CONTAINER
            },
            visibilityModifiers = setOf(VisibilityModifier.ALL),
            data = globalGameData,
            additionalData = result
        )
    }

    private fun whoTouchedLast(
        target: WithStringId?,
        data: GlobalGameData
    ): PersonWithTime? {
        val activity = activityRepository
            .findByGameId(data.game.inGameId())
            .filter {
                it.activityType in relatedActivityTypes &&
                        it.relatedGameObjectType in relatedTargetTypes &&
                        it.relatedGameObjectId.toString() == target?.stringId()
            }.maxByOrNull { it.gameTime }
        return activity?.let {
            val user = it.sourceUserId?.let {
                data.users[it]
            }
            val time = it.gameTime
            PersonWithTime(
                user = user?.let { userNotNull ->
                    UserActivityView(
                        id = userNotNull.inGameId(),
                        nickName = userNotNull.nickName,
                        skin = RedisUserSkinSetting(userNotNull.originalSkin)
                    )
                },
                currentTime = data.game.inGameId(),
                eventTime = time,
            )
        }
    }

}