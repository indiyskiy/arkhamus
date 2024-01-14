package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.logic.CustomGameLogic
import com.arkhamusserver.arkhamus.logic.DefaultGameLogic
import com.arkhamusserver.arkhamus.logic.SingleGameLogic
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("game")
class GameController(
    private val customGameLogic: CustomGameLogic,
    private val singleGameLogic: SingleGameLogic,
    private val gameLogic: DefaultGameLogic,
) {
    @GetMapping("{gameId}")
    fun getGame(@PathVariable gameId: Long): ResponseEntity<GameSessionDto> {
        val gameSession = customGameLogic.findGame(gameId)
        return ResponseEntity.ok(gameSession)
    }

    @GetMapping("byplayer/{playerId}")
    fun findUsersOpenGame(@PathVariable playerId: Long): ResponseEntity<GameSessionDto> {
        val gameSession = customGameLogic.findUsersOpenGame(playerId)
        return ResponseEntity.ok(gameSession)
    }

    @PostMapping
    fun createCustom(): ResponseEntity<GameSessionDto> {
        val gameSession = customGameLogic.createGame()
        return ResponseEntity.ok(gameSession)
    }

    @PostMapping("single")
    fun createSingle(): ResponseEntity<GameSessionDto> {
        val gameSession = singleGameLogic.createGame()
        return ResponseEntity.ok(gameSession)
    }

    @PutMapping("{gameId}/connect")
    fun connect(
        @PathVariable gameId: Long,
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = customGameLogic.connectToGame(gameId)
        return ResponseEntity.ok(gamesSession)
    }

    @PutMapping("{gameId}")
    fun update(
        @PathVariable gameId: Long,
        @RequestBody gameSession: GameSessionDto
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = customGameLogic.updateLobby(gameId, gameSession)
        return ResponseEntity.ok(gamesSession)
    }

    @PutMapping("{gameId}/start")
    fun start(
        @PathVariable gameId: Long,
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = gameLogic.start(gameId)
        return ResponseEntity.ok(gamesSession)
    }

}