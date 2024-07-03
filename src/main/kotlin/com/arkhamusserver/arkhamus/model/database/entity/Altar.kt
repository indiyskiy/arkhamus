package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class Altar(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var point: PGpoint,
    var inGameId: Long? = null,
    var interactionRadius: Double? = null,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null
)