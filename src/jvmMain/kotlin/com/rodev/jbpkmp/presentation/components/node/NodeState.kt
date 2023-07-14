package com.rodev.jbpkmp.presentation.components.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.data.NodeEntity

class NodeState(
    val nodeEntity: NodeEntity
) {
    var x by mutableStateOf(nodeEntity.x)
    var y by mutableStateOf(nodeEntity.y)
}