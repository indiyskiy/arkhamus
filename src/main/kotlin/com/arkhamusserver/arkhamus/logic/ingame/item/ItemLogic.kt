package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.view.dto.ingame.ItemInformationDto
import com.arkhamusserver.arkhamus.view.dto.ingame.RecipeDto
import com.arkhamusserver.arkhamus.view.maker.ingame.ItemInformationDtoMaker
import com.arkhamusserver.arkhamus.view.maker.ingame.RecipeToRecipeDtoMaker
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import org.springframework.stereotype.Component

@Component
class ItemLogic(
    private val itemsDtoMaker: ItemInformationDtoMaker,
    private val recipeDtoMaker: RecipeToRecipeDtoMaker,
    private val itemToRecipeResolver: ItemToRecipeResolver
) {
    fun listAllItems(): List<ItemInformationDto> =
        itemsDtoMaker.convert(Item.values().toList())

    fun listAllRecipes(): List<RecipeDto> =
        recipeDtoMaker.convert(
            Item.values().map {
                RecipeToRecipeDtoMaker.Data(
                    it,
                    itemToRecipeResolver.resolve(it)
                )
            }.filter {
                it.recipe.ingredients.isNotEmpty()
            }
        )
}