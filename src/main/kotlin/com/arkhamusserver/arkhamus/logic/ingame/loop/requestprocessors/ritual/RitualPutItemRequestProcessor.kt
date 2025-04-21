package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RitualPutItemRequestProcessor(
    private val ritualHandler: RitualHandler,
) : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is RitualPutItemRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val ritualPutItemRequestProcessData = requestDataHolder.requestProcessData as RitualPutItemRequestProcessData
        val item = ritualPutItemRequestProcessData.item
        val itemNumber = ritualPutItemRequestProcessData.itemNumber
        val altarHolder = globalGameData.altarHolder

        if (ritualPutItemRequestProcessData.canPut) {
            ritualHandler.takeItemForRitual(
                item = item!!,
                itemNumber = itemNumber,
                altarHolder = altarHolder,
                user = ritualPutItemRequestProcessData.gameUser!!
            )

            if (altarHolder != null && ritualHandler.isAllItemsPut(altarHolder) == true) {
                ritualHandler.processAllItemsPut(globalGameData, altarHolder, ongoingEvents)
            }
            ritualPutItemRequestProcessData.executedSuccessfully = true
        }
    }

}

