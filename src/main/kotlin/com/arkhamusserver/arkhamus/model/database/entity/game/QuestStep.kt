package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.*

@Entity
data class QuestStep(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var stepNumber: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questId", referencedColumnName = "id", nullable = false)
    var quest: Quest?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levelTaskId", referencedColumnName = "id", nullable = false)
    var levelTask: LevelTask,
)