package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameActivity
import org.springframework.stereotype.Repository

@Repository
class InGameActivityRepository : RamCrudRepository<InGameActivity>()