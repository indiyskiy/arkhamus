package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class StartMarker(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var point: PGpoint,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level
)