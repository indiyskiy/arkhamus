package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.GameState
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class GameSession(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null,
    var state: GameState,
    var lobbySize: Int? = null
)