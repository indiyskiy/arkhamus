package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.tech.ISawTheEndOfTimesRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ISawTheEndOfTimesRequestProcessor : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ISawTheEndOfTimesRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is ISawTheEndOfTimesRequestGameData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        with(requestDataHolder.requestProcessData as ISawTheEndOfTimesRequestGameData) {
            if (this.gameEnded && this.gameUser?.techData?.sawTheEndOfTimes != true) {
                logger.info("set sawTheEndOfTimes true")
                this.gameUser?.let { it.techData.sawTheEndOfTimes = true }
            }
        }
    }
}