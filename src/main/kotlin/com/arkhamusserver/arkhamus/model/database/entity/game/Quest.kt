package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import jakarta.persistence.*

@Entity
data class Quest(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "quest")
    var questSteps: MutableList<QuestStep> = mutableListOf(),
    var questState: QuestState = QuestState.DRAFT,
    var name: String = "new quest",
    @ManyToOne
    @JoinColumn(name = "startQuestGiverId", nullable = false)
    var startQuestGiver: QuestGiver,
    @ManyToOne
    @JoinColumn(name = "endQuestGiverId", nullable = false)
    var endQuestGiver: QuestGiver,
    var dificulty: QuestDifficulty = QuestDifficulty.VERY_EASY
) {
    fun addQuestStep(questStep: QuestStep) {
        this.questSteps.add(questStep)
        questStep.quest = this
    }

    fun removeQuestStep(questStep: QuestStep) {
        this.questSteps.remove(questStep) //remove an item
        questStep.quest = null
    }
}