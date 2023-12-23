package com.arkhamusserver.arkhamus.logic.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class IngredientDto(
    var item: Item? = null,
    var number: Int? = null
)

