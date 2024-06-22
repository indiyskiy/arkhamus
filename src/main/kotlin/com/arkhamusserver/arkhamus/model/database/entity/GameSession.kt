package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
data class GameSession(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @CreationTimestamp
    var creationTimestamp: Timestamp? = null,
    var startedTimestamp: Timestamp? = null,
    var finishedTimestamp: Timestamp? = null,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gameSession")
    var usersOfGameSession: List<UserOfGameSession> = emptyList(),

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gameSettingsId")
    var gameSessionSettings: GameSessionSettings,

    var state: GameState,
    var gameEndReason: GameEndReason? = null,
    var gameType: GameType,
    var god: God? = null,
    var token: String? = null
)