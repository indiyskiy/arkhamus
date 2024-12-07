package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ObjectWithTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.OpenCrafterRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OpenCrafterRequestProcessor(
    private val redisCrafterRepository: RedisCrafterRepository,
    private val objectWithTagsHandler: ObjectWithTagsHandler,
    private val activityHandler: ActivityHandler
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is OpenCrafterRequestGameData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as OpenCrafterRequestGameData
        val user = globalGameData.users[requestDataHolder.userAccount.id]!!
        val crafter = gameData.crafter
        if ((crafter.state == MapObjectState.ACTIVE) && (crafter.holdingUser == null)) {
            crafter.holdingUser = user.inGameId()
            crafter.state = MapObjectState.HOLD
            objectWithTagsHandler.processObject(crafter, user, globalGameData)
            redisCrafterRepository.save(crafter)

            activityHandler.addUserWithTargetActivity(
                globalGameData.game.inGameId(),
                ActivityType.CRAFTER_OPENED,
                user,
                globalGameData.game.globalTimer,
                GameObjectType.CRAFTER,
                crafter,
                null
            )
        }
    }
}