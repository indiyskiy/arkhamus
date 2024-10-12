package com.arkhamusserver.arkhamus.logic.ingame.item.recipe

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class Ingredient(
    var item: Item,
    var number: Int
)

