package com.arkhamusserver.arkhamus.view.dto.netty.response

interface ActionResponse {
    fun isExecutedSuccessfully(): Boolean
    fun isFirstTime(): Boolean
}