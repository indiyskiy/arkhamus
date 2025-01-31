package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.banvote

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.banvote.PayForVoteRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameVoteSpotRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PayForVoteRequestProcessor(
    private val inventoryHandler: InventoryHandler,
    private val inGameVoteSpotRepository: InGameVoteSpotRepository,
    private val activityHandler: ActivityHandler
) : NettyRequestProcessor {


    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is PayForVoteRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as PayForVoteRequestProcessData
        val voteSpot = gameData.voteSpot
        if (gameData.canPay &&
            voteSpot != null
        ) {
            inventoryHandler.consumeItems(gameData.gameUser!!, voteSpot.costItem, voteSpot.costValue)
            voteSpot.voteSpotState = VoteSpotState.OPEN
            inGameVoteSpotRepository.save(voteSpot)
            gameData.successfullyPaid = true

            activityHandler.addUserWithTargetActivity(
                globalGameData.game.inGameId(),
                ActivityType.BAN_SPOT_PAYED,
                gameData.gameUser,
                globalGameData.game.globalTimer,
                GameObjectType.VOTE_SPOT,
                gameData.voteSpot,
                null
            )
        }
    }

}
