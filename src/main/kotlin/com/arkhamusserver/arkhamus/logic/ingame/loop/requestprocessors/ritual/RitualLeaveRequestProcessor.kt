package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualLeaveRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAltarHolderRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RitualLeaveRequestProcessor(
    private val inGameAltarHolderRepository: InGameAltarHolderRepository
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(RitualLeaveRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is RitualLeaveRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val ritualLeaveRequestProcessData = requestDataHolder.requestProcessData as RitualLeaveRequestProcessData
        val altarHolder = ritualLeaveRequestProcessData.altarHolder
        val user = ritualLeaveRequestProcessData.gameUser!!

        if (ritualLeaveRequestProcessData.userInRitual) {
            logger.info("leave ritual")
            if (altarHolder != null) {
                altarHolder.usersToKick += user.inGameId()
                inGameAltarHolderRepository.save(altarHolder)
            }
            ritualLeaveRequestProcessData.executedSuccessfully = true
        }
    }

}

