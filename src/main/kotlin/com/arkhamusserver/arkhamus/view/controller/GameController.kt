package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.logic.GameLogic
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("game")
class GameController(
    private val gameLogic: GameLogic,
) {
    @GetMapping("{gameId}")
    fun getGame(@PathVariable gameId: Long): ResponseEntity<GameSessionDto> {
        val gameSession = gameLogic.findGame(gameId)
        return ResponseEntity.ok(gameSession)
    }

    @GetMapping("byplayer/{playerId}")
    fun findUsersOpenGame(@PathVariable playerId: Long): ResponseEntity<GameSessionDto> {
        val gameSession = gameLogic.findUsersOpenGame(playerId)
        return ResponseEntity.ok(gameSession)
    }

    @PostMapping
    fun create(): ResponseEntity<GameSessionDto> {
        val gameSession = gameLogic.createGame()
        return ResponseEntity.ok(gameSession)
    }

    @PutMapping("{gameId}/connect")
    fun connect(
        @PathVariable gameId: Long,
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = gameLogic.connectToGame(gameId)
        return ResponseEntity.ok(gamesSession)
    }

    @PutMapping("{gameId}")
    fun update(
        @PathVariable gameId: Long,
        @RequestBody gameSession: GameSessionDto
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = gameLogic.updateLobby(gameId, gameSession)
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