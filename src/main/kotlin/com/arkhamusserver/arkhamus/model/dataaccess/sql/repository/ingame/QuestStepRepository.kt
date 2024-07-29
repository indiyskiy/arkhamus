package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.QuestStep
import org.springframework.data.repository.CrudRepository

interface QuestStepRepository : CrudRepository<QuestStep, Long>