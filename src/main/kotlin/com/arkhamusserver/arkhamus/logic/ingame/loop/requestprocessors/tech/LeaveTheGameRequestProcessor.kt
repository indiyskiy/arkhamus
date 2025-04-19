package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.tech

import com.arkhamusserver.arkhamus.config.CultpritsUserState
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.tech.LeaveTheGameRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LeaveTheGameRequestProcessor(
    private val userStatusService: UserStatusService
) : NettyRequestProcessor {

    companion object {
        private val logger = LoggingUtils.getLogger<LeaveTheGameRequestProcessor>()
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is LeaveTheGameRequestGameData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        with(requestDataHolder.requestProcessData as LeaveTheGameRequestGameData) {
            if (this.canLeaveTheGame) {
                logger.info("user ${gameUser!!.inGameId()} left the game")
                this.gameUser.techData.leftTheGame = true
                userStatusService.updateUserStatus(gameUser.inGameId(), CultpritsUserState.ONLINE)
            }
        }
    }
}