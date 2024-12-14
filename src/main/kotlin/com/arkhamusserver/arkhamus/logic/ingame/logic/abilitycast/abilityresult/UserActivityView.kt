package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult

import com.arkhamusserver.arkhamus.model.redis.parts.RedisUserSkinSetting

data class UserActivityView(
    var id: Long? = null,
    var nickName: String? = null,
    var skin: RedisUserSkinSetting? = null,
)