package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.ArkhamusApplication
import com.arkhamusserver.arkhamus.logic.GameLogic
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("game")
class GameController(
    private val gameLogic: GameLogic,
) {
    private val log: Logger = LoggerFactory.getLogger(ArkhamusApplication::class.java)

    @GetMapping("{gameId}")
    fun getGame(@PathVariable gameId: Long): ResponseEntity<GameSession> {
        val gameSession = gameLogic.findGame(gameId)
        return ResponseEntity.ok(gameSession)
    }

    @GetMapping("byPlayer/{playerId}")
    fun findUsersOpenGame(@PathVariable playerId: Long): ResponseEntity<GameSession> {
        val gameSession = gameLogic.findUsersOpenGame(playerId)
        return ResponseEntity.ok(gameSession)
    }

    @PostMapping("create/{playerId}")
    fun create(@PathVariable playerId: Long): ResponseEntity<GameSession> {
        log.info("create game request: {}", playerId)
        val gameSession = gameLogic.createGame(playerId)
        return ResponseEntity.ok(gameSession)
    }

    @PutMapping("connect/{playerId}/{gameId}")
    fun connect(
        @PathVariable playerId: Long,
        @PathVariable gameId: Long,
    ): ResponseEntity<GameSession> {
        log.info("connect request: {} to {}", playerId, gameId)
        val gamesSession = gameLogic.connectToGame(playerId, gameId)
        return ResponseEntity.ok(gamesSession)
    }

    @PutMapping("start/{playerId}/{gameId}")
    fun start(
        @PathVariable playerId: Long,
        @PathVariable gameId: Long,
    ): ResponseEntity<GameSession> {
        log.info("start game: {} to {}", playerId, gameId)
        val gamesSession = gameLogic.start(playerId, gameId)
        return ResponseEntity.ok(gamesSession)
    }

}