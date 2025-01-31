package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameQuestGiver
import org.springframework.stereotype.Repository

@Repository
class InGameQuestGiverRepository : RamCrudRepository<InGameQuestGiver>()