package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

data class MyGameUserResponse(
    val id: Long,
    val nickName: String,
    val madness: Double,
    var madnessNotches: List<Double>,
    val x: Double,
    val y: Double,
    val z: Double,
    val stateTags: Set<String> = emptySet(),
    var quests: List<UserQuestResponse> = emptyList()
) {
    constructor(gameUser: RedisGameUser, quests: List<UserQuestResponse>) : this(
        id = gameUser.userId,
        nickName = gameUser.nickName,
        madness = gameUser.madness,
        madnessNotches = gameUser.madnessNotches,
        x = gameUser.x,
        y = gameUser.y,
        z = gameUser.z,
        stateTags = gameUser.stateTags,
        quests = quests
    )
}