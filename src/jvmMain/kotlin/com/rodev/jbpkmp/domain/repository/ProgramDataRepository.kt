package com.rodev.jbpkmp.domain.repository

import com.rodev.jbpkmp.domain.model.ProgramData

interface ProgramDataRepository {
    fun save(data: ProgramData)
    fun load(): ProgramData
}

inline fun ProgramDataRepository.update(block: ProgramData.() -> Unit) {
    with(load()) {
        block(this)
        save(this)
    }
}