package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameUserQuestProgress
import org.springframework.stereotype.Repository

@Repository
class InGameUserQuestProgressRepository : RamCrudRepository<InGameUserQuestProgress>()