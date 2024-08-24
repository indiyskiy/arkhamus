package com.arkhamusserver.arkhamus.view.dto.netty.response.tech

import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse

class NettyResponseAuth(
    val message: AuthState = AuthState.FAIL,
    val reason: String = "",
    tick: Long = -1,
    userId: Long,
    myGameUser: MyGameUserResponse,
    allGameUsers: List<GameUserResponse>,
) : NettyResponse(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = allGameUsers,
    ongoingEvents = emptyList(),
    shortTimeEvents = emptyList(),
    availableAbilities = emptyList(),
    ongoingCraftingProcess = emptyList(),
    userInventory = emptyList(),
    containers = emptyList(),
    crafters = emptyList(),
    inZones = emptyList(),
    clues = emptyList(),
    type = NettyResponseAuth::class.java.simpleName
)