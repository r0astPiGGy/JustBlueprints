package com.rodev.jbpkmp.domain.source

import com.rodev.generator.action.entity.PinType

interface PinTypeDataSource {

    fun getPinTypeById(id: String): PinType?

}

operator fun PinTypeDataSource.get(id: String): PinType? = getPinTypeById(id)