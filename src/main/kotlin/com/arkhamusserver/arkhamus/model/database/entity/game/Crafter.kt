package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType
import com.arkhamusserver.arkhamus.model.redis.WithPoint
import jakarta.persistence.*

@Entity
data class Crafter(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var interactionRadius: Double,
    var x: Double,
    var y: Double,
    var z: Double,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level,
    @Enumerated(EnumType.STRING)
    var crafterType: CrafterType,
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