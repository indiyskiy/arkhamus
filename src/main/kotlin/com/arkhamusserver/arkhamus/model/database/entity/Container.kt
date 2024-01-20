package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.*

@Entity
data class Container(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var interactionRadius: Double? = null,
    var x: Double? = null,
    var y: Double? = null,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null
)