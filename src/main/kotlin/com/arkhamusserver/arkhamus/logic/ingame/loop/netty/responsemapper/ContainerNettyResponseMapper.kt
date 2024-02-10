package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyContainerCell
import org.springframework.stereotype.Component

@Component
class ContainerNettyResponseMapper : NettyResponseMapper {

    private val itemMap = Item.values().associateBy { it.getId().toString() }
    override fun acceptClass(gameResponseMessage: GameResponseMessage): Boolean =
        gameResponseMessage::class.java == ContainerGameResponse::class.java

    override fun accept(gameResponseMessage: GameResponseMessage): Boolean = true

    override fun process(
        gameResponseMessage: GameResponseMessage,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): ContainerNettyResponse {
        with(gameResponseMessage as ContainerGameResponse) {
            val mappedItem = this.container.items.map {
                itemMap[it.key]!! to it.value
            }
            val containerCells = mappedItem.map {
                NettyContainerCell(it.first.getId()).apply {
                    this.number = it.second
                }
            }
            return ContainerNettyResponse(
                tick = nettyRequestMessage.baseRequestData().tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponseMessage(
                    id = user.id!!,
                    nickName = user.nickName!!,
                    x = gameResponseMessage.gameUser.x!!,
                    y = gameResponseMessage.gameUser.y!!
                ),
                allGameUser = emptyList()
            ).apply {
                this.containerCells = containerCells
            }
        }
    }

}