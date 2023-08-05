package com.rodev.jbpkmp.domain.repository

import com.rodev.generator.action.entity.SelectorType
import com.rodev.jbpkmp.domain.model.SelectorGroup

interface SelectorDataSource {

    fun getSelectorByType(type: SelectorType): SelectorGroup

}