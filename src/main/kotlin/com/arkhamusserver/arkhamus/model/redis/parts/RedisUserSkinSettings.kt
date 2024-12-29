package com.arkhamusserver.arkhamus.model.redis.parts

import com.arkhamusserver.arkhamus.model.enums.SkinColor

data class RedisUserSkinSetting(
    var skinColor: SkinColor,
) {
    constructor(redisUserSkinSetting: RedisUserSkinSetting) : this(
        redisUserSkinSetting.skinColor
    )
}