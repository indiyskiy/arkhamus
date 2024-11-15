package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.fasterxml.uuid.Generators

fun generateRandomId() = Generators.timeBasedEpochGenerator().generate().toString()