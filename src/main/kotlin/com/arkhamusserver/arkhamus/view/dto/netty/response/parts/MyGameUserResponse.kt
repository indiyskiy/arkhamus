package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

data class MyGameUserResponse(
    val id: Long,
    val nickName: String,
    val madness: Double,
    var madnessNotches: List<Double>,
    val x: Double,
    val y: Double,
    val stateTags : Set<String> = emptySet(),
    var availableQuests: Set<Long> = emptySet()
) {
    constructor(gameUser: RedisGameUser) : this(
        id = gameUser.userId,
        nickName = gameUser.nickName,
        madness = gameUser.madness,
        madnessNotches = gameUser.madnessNotches,
        x = gameUser.x,
        y = gameUser.y,
        stateTags = gameUser.stateTags,
        availableQuests = gameUser.availableQuestIds
    )
}