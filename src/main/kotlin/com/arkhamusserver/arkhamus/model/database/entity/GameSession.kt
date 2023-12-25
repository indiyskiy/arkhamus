package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
data class GameSession(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null,
    var state: GameState,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameSession")
    var usersOfGameSession: List<UserOfGameSession>? = null,
    var lobbySize: Int? = null,
    var numberOfCultists: Int? = null,
    var god: God? = null,
    @CreationTimestamp
    var creationTimestamp: Timestamp? = null,
)