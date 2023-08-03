package com.rodev.jbpkmp.domain.model

data class NodeEntity(
    val id: String,
    val header: String,
    val subHeader: String? = null,
    val headerColor: Int,
    val iconPath: String
)
