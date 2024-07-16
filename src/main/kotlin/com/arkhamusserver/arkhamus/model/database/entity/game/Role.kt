package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.*

@Entity
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @Column(nullable = false, unique = true)
    var name: String
)