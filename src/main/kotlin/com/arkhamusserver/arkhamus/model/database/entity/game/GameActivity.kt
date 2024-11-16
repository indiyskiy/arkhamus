package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class GameActivity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var x: Double,
    var y: Double,
    var z: Double,
    val gameTime: Long,
    @Enumerated(EnumType.STRING)
    val relatedGameObjectType: GameObjectType?,
    val relatedGameObjectId: Long?,
    val relatedEventId: Long?,
    @ManyToOne
    @JoinColumn(name = "gameSessionId", nullable = false)
    var gameSession: GameSession,
    @ManyToOne
    @JoinColumn(name = "userOfGameSessionId", nullable = false)
    var userOfGameSession: UserOfGameSession,
)