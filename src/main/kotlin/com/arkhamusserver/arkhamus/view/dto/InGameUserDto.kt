package com.arkhamusserver.arkhamus.view.dto

data class InGameUserDto(
    var role: RoleDto? = null,
    var userId: Long? = null,
    var steamId: String? = null,
    var nickName: String? = null,
    var isHost: Boolean = false,
    var gameSkin: UserSkinDto? = null
) {
    override fun toString(): String {
        return nickName ?: "-"
    }
}