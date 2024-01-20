package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.AuthState

data class NettyResponseAuth (
    val message: AuthState = AuthState.FAIL,
): NettyResponseMessage
