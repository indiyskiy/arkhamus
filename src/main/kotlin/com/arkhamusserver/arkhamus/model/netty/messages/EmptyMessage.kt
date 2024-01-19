package com.arkhamusserver.arkhamus.model.netty.messages

data class EmptyMessage(
    var type: String? = null
): NettyMessage