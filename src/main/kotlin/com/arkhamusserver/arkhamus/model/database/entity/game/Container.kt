package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class Container(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var point: PGpoint,
    var interactionRadius: Double,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null
)