package com.rodev.jbpkmp.domain.source

import com.rodev.generator.action.entity.ActionDetails

interface ActionDetailsDataSource {

    fun getActionDetailsById(id: String): ActionDetails?

}

operator fun ActionDetailsDataSource.get(id: String) = getActionDetailsById(id)