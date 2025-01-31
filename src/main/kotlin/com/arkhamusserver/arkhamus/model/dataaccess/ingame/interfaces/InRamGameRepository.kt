package com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces

import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import org.springframework.stereotype.Repository

@Repository
class InRamGameRepository : RamCrudRepository<InRamGame>()