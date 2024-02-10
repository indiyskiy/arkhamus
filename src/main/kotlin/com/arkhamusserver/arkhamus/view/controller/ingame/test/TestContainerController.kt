package com.arkhamusserver.arkhamus.view.controller.ingame.test

import com.arkhamusserver.arkhamus.logic.ingame.test.TestContainerLogic
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerNettyResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("container")
class TestContainerController(
    private val containerLogic: TestContainerLogic
) {
    @GetMapping("{gameId}/{containerId}")
    fun getAllItems(
        @PathVariable containerId: Long,
        @PathVariable gameId: Long,
    ): ResponseEntity<ContainerNettyResponse> {
        val items = containerLogic.getContainerByUserAndId(gameId, containerId)
        return ResponseEntity.ok(items)
    }
}