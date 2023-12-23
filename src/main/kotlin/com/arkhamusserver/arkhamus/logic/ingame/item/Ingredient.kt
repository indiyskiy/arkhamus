package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class Ingredient(
    var item: Item? = null,
    var number: Int? = null
)

fun ingridient(item: Item, number: Int) =
    Ingredient().apply {
        this.item = item
        this.number = number
    }
