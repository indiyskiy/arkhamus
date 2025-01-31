package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.springframework.stereotype.Repository

@Repository
class InGameAbilityCastRepository : RamCrudRepository<InGameAbilityCast>()