package com.rodev.jbpkmp.domain.repository

import com.rodev.generator.action.entity.NodeType

interface NodeTypeDataSource {

    fun getNodeTypeById(id: String): NodeType?

}

operator fun NodeTypeDataSource.get(id: String): NodeType? = getNodeTypeById(id)