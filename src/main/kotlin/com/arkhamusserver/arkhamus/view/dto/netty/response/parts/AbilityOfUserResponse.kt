package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

data class AbilityOfUserResponse(
    val abilityId: Int? = null,
    val maxCooldown: Long? = null,
    val cooldown: Long = 0,
    val charges: Int? = null,
)