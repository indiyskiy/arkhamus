package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser

data class MyGameUserResponse(
    val id: Long,
    val nickName: String,
    val madness: Double,
    var madnessNotches: List<Double>,
    var madnessDebuffs: Set<String>,
    val x: Double,
    val y: Double,
    val z: Double,
    val stateTags: Set<UserStateTag> = emptySet(),
    var quests: List<UserQuestResponse> = emptyList()
) {
    constructor(gameUser: InGameGameUser, quests: List<UserQuestResponse>) : this(
        id = gameUser.inGameId(),
        nickName = gameUser.nickName,
        madness = gameUser.madness,
        madnessNotches = gameUser.madnessNotches,
        madnessDebuffs = gameUser.madnessDebuffs,
        x = gameUser.x,
        y = gameUser.y,
        z = gameUser.z,
        stateTags = gameUser.stateTags,
        quests = quests
    )
}