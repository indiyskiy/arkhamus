package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType
import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class Crafter(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var interactionRadius: Double,
    var point: PGpoint,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level,
    var crafterType: CrafterType,
)