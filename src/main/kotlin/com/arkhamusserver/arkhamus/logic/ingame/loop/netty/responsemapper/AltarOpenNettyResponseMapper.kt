package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AltarOpenRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AltarPolling
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.VoteForGod
import org.springframework.stereotype.Component

@Component
class AltarOpenNettyResponseMapper : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == AltarOpenRequestProcessData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder
    ): AltarOpenNettyResponse {
        (requestProcessData as AltarOpenRequestProcessData).let {
            val voteProcessOpen = isVoteProcessOpen(requestProcessData)
            return AltarOpenNettyResponse(
                altarPollingProgress = (requestProcessData.altarPolling?.mapVotes(voteProcessOpen)),
                canIVote = canIVote(voteProcessOpen, requestProcessData, user),
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(it.gameUser!!),
                otherGameUsers = it.otherGameUsers.map { gameUser ->
                    NettyGameUserResponse(
                        id = gameUser.userId,
                        nickName = gameUser.nickName,
                        x = gameUser.x,
                        y = gameUser.y
                    )
                },
                ongoingEvents = requestProcessData.visibleOngoingEvents.map { event ->
                    OngoingEventResponse(event)
                },
                availableAbilities = requestProcessData.availableAbilities,
                ongoingCraftingProcess = requestProcessData.ongoingCraftingProcess,
                userInventory = requestProcessData.visibleItems,
                containers = requestProcessData.containers
            )
        }
    }

    private fun canIVote(
        voteProcessOpen: Boolean,
        requestProcessData: AltarOpenRequestProcessData,
        user: UserAccount
    ) = voteProcessOpen &&
            ((requestProcessData.altarPolling?.userVotes?.get(user.id)) == null)

    private fun isVoteProcessOpen(requestProcessData: AltarOpenRequestProcessData) =
        (requestProcessData.altarPolling?.state == MapAltarPollingState.ONGOING) &&
                (requestProcessData.altarHolder?.state == MapAltarState.VOTING)


}

private fun RedisAltarPolling?.mapVotes(isVoteProcessOpen: Boolean): AltarPolling? =
    if (isVoteProcessOpen) {
        this?.userVotes
            ?.map { it }
            ?.groupBy { it.value }
            ?.map {
                VoteForGod(
                    godId = it.key,
                    voteCount = it.value.size
                )
            }?.let {
                AltarPolling(it)
            }
    } else {
        null
    }

