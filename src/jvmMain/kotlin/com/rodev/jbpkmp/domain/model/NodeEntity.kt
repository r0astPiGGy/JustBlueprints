package com.rodev.jbpkmp.domain.model

interface NodeEntity {
    val id: String
    val header: String
    val subHeader: String?
    val headerColor: Int
    val iconPath: String
}

private data class NodeEntityImpl(
    override val id: String,
    override val header: String,
    override val subHeader: String?,
    override val headerColor: Int,
    override val iconPath: String
) : NodeEntity

fun NodeEntity(
    id: String,
    header: String,
    subHeader: String? = null,
    headerColor: Int,
    iconPath: String
) : NodeEntity = NodeEntityImpl(
    id,
    header,
    subHeader,
    headerColor,
    iconPath
)
