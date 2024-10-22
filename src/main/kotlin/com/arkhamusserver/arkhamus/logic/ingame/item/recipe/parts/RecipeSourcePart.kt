package com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe

interface RecipeSourcePart {
     fun recipes(): List<Recipe>
}