package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameAltar
import org.springframework.stereotype.Repository

@Repository
class InGameAltarRepository : RamCrudRepository<InGameAltar>()