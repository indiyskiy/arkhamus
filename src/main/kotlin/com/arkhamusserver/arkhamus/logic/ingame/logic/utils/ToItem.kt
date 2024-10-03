package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

fun Int?.toItemName(): String =
    this?.toItem()?.name ?: "-"

fun Int.toItem(): Item =
    Item.values().first { it.id == this }

