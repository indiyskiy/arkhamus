package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class ContainerNettyResponseMapper : NettyResponseMapper {

    private val itemMap = Item.values().associateBy { it.getId().toString() }
    override fun acceptClass(gameResponseMessage: GameData): Boolean =
        gameResponseMessage::class.java == ContainerGameData::class.java

    override fun accept(gameResponseMessage: GameData): Boolean = true

    override fun process(
        gameData: GameData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): ContainerNettyResponse {
        with(gameData as ContainerGameData) {
            val mappedItem = this.container.items.map {
                itemMap[it.key]!! to it.value
            }
            val containerCells = mappedItem.map {
                NettyContainerCell(it.first.getId()).apply {
                    this.number = it.second
                }
            }
            return ContainerNettyResponse(
                tick = gameData.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponseMessage(
                    id = user.id!!,
                    nickName = user.nickName!!,
                    x = gameData.gameUser!!.x,
                    y = gameData.gameUser.y
                ),
                otherGameUsers = gameData.otherGameUsers.map {
                    NettyGameUserResponseMessage(
                        id = it.userId,
                        nickName = it.nickName,
                        x = it.x,
                        y = it.y
                    )
                },
                ongoingEffects = gameData.visibleOngoingEffects.map {
                    OngoingEventResponse(it.event.type)
                }
            ).apply {
                this.containerCells = containerCells
            }
        }
    }

}