package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.model.enums.SkinColor

data class UserSkinDto(
    var userId: Long? = null,
    var skinColor: SkinColor? = null
)