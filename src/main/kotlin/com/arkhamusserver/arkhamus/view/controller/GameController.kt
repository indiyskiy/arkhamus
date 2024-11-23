package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.config.UpdateUserState
import com.arkhamusserver.arkhamus.config.UserState
import com.arkhamusserver.arkhamus.logic.CustomGameLogic
import com.arkhamusserver.arkhamus.logic.DefaultGameLogic
import com.arkhamusserver.arkhamus.logic.SingleGameLogic
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("game")
class GameController(
    private val customGameLogic: CustomGameLogic,
    private val singleGameLogic: SingleGameLogic,
    private val gameLogic: DefaultGameLogic,
) {
    @UpdateUserState(UserState.IN_LOBBY)
    @GetMapping("{gameId}")
    fun getGame(@PathVariable gameId: Long): ResponseEntity<GameSessionDto> {
        val gameSession = customGameLogic.findGame(gameId)
        return ResponseEntity.ok(gameSession)
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @GetMapping("byToken/{token}")
    fun getGameByToken(@PathVariable token: String): ResponseEntity<GameSessionDto> {
        val gameSession = customGameLogic.findGame(token)
        return ResponseEntity.ok(gameSession)
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @GetMapping("byplayer/{playerId}")
    fun findUsersOpenGame(@PathVariable playerId: Long): ResponseEntity<GameSessionDto> {
        val gameSession = customGameLogic.findUsersOpenGame(playerId)
        return ResponseEntity.ok(gameSession)
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @PostMapping
    fun createCustom(): ResponseEntity<GameSessionDto> {
        val gameSession = customGameLogic.createGame()
        return ResponseEntity.ok(gameSession)
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @PostMapping("single")
    fun createSingle(): ResponseEntity<GameSessionDto> {
        val gameSession = singleGameLogic.createGame()
        return ResponseEntity.ok(gameSession)
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @PutMapping("{gameId}/connect")
    fun connect(
        @PathVariable gameId: Long,
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = customGameLogic.connectToGame(gameId)
        return ResponseEntity.ok(gamesSession)
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @PutMapping("byToken/{token}/connect")
    fun connect(
        @PathVariable token: String,
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = customGameLogic.connectToGameByToken(token)
        return ResponseEntity.ok(gamesSession)
    }

    @UpdateUserState(UserState.ONLINE)
    @PutMapping("{gameId}/disconnect")
    fun disconnect(
        @PathVariable gameId: Long,
    ): ResponseEntity.BodyBuilder {
        gameLogic.disconnectTransactional()
        return ResponseEntity.ok()
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @PutMapping("{gameId}")
    fun update(
        @PathVariable gameId: Long,
        @RequestBody gameSessionSettingsDto: GameSessionSettingsDto
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = customGameLogic.updateLobby(gameId, gameSessionSettingsDto)
        return ResponseEntity.ok(gamesSession)
    }

    @UpdateUserState(UserState.IN_GAME)
    @PutMapping("{gameId}/start")
    fun start(
        @PathVariable gameId: Long,
    ): ResponseEntity<GameSessionDto> {
        val gamesSession = gameLogic.start(gameId)
        return ResponseEntity.ok(gamesSession)
    }

}