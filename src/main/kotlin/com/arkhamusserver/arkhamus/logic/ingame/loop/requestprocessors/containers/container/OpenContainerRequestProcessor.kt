package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ObjectWithTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.OpenContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameContainerRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OpenContainerRequestProcessor(
    private val inGameContainerRepository: InGameContainerRepository,
    private val objectWithTagsHandler: ObjectWithTagsHandler,
    private val activityHandler: ActivityHandler
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is OpenContainerRequestGameData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as OpenContainerRequestGameData
        val gameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val container = gameData.container
        if ((container.state == MapObjectState.ACTIVE) && (container.holdingUser == null)) {
            container.holdingUser = gameUser.inGameId()
            container.state = MapObjectState.HOLD
            objectWithTagsHandler.processObject(container, gameUser, globalGameData)
            inGameContainerRepository.save(container)
            activityHandler.addUserWithTargetActivity(
                globalGameData.game.inGameId(),
                ActivityType.CONTAINER_OPENED,
                gameUser,
                globalGameData.game.globalTimer,
                GameObjectType.CONTAINER,
                container,
                null
            )
        }
    }
}