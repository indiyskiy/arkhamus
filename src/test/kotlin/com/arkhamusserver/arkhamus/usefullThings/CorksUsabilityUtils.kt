package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.logic.ingame.item.GodCorkToReceiptResolver
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CorksUsabilityUtils() {
    @Autowired
    lateinit var resolver: GodCorkToReceiptResolver

    @Test
    fun contextLoads() {
        val itemsToGod = Item.values().map {
            it to resolver.resolve(it)
        }
        val itemsUsability = itemsToGod
            .map { it.second }
            .flatten()
            .groupBy { it.name }
            .map { it.key to it.value.size }
        println(Item.values().joinToString("\r\n") { item ->
            with(itemsUsability.firstOrNull { it.first == item.name }) {
               this?.let {"$first - $second"}?:"${item.name} - 0"
            }
        })
    }

}