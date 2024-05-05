package com.arkhamusserver.arkhamus.view.dto.netty.request

class CraftProcessRequestMessage(
    var recipeId: Int,
    var crafterId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)