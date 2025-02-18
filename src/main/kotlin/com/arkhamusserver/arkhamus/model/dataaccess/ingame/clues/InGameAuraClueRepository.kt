package com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameAuraClue
import org.springframework.stereotype.Repository

@Repository
class InGameAuraClueRepository : RamCrudRepository<InGameAuraClue>()