package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse

class NettyGameStartedResponse(
    val message: String = "STARTED",
    tick: Long = 0,
    userId: Long,
    myGameUser: MyGameUserResponse,
    allGameUsers: List<GameUserResponse>,
) : NettyResponse(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = allGameUsers,
    ongoingCraftingProcess = emptyList(),
    ongoingEvents = emptyList(),
    availableAbilities = emptyList(),
    userInventory = emptyList(),
    containers = emptyList(),
    inZones = emptyList(),
    type = NettyGameStartedResponse::class.java.simpleName
)