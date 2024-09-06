package com.arkhamusserver.arkhamus.model.enums.ingame

enum class CantVoteReason(
    val priority: Int
) {
    BANNED(1000),
    MAD(100),
    CANT_PAY(0)
}