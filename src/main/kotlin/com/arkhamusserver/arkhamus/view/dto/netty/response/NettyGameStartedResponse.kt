package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

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
    shortTimeEvents = emptyList(),
    availableAbilities = emptyList(),
    userInventory = emptyList(),
    containers = emptyList(),
    crafters = emptyList(),
    inZones = emptyList(),
    doors = emptyList(),
    clues = ExtendedCluesResponse(emptyList(), emptyList()),
    lanterns = emptyList(),
    easyVoteSpots = emptyList(),
    questGivers = emptyList(),
    questSteps = emptyList(),
    statuses = emptyList(),
    altars = emptyList(),
    type = NettyGameStartedResponse::class.java.simpleName
)