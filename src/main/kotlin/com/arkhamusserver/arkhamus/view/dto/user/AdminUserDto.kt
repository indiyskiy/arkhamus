package com.arkhamusserver.arkhamus.view.dto.user

data class AdminUserDto(
    val userId: Long,
    val nickName: String,
    val email: String,
    val creation: String?
)