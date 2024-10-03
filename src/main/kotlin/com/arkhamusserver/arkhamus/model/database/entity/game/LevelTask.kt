package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import jakarta.persistence.*

@Entity
data class LevelTask(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level,
    var name: String,
    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double,
) : WithPoint {
    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }
}