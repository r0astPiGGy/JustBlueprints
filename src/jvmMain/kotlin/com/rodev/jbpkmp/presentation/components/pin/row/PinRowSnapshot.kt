package com.rodev.jbpkmp.presentation.components.pin.row

import androidx.compose.ui.geometry.Offset
import com.rodev.jbpkmp.presentation.components.pin.PinState

interface SnapshotRequester {

    val snapshotRequested: Boolean

    fun addSnapshot(snapshot: PinRowSnapshot)

}

fun PinRowSnapshot(
    pinRowState: PinRowState,
    pinState: PinState,
    topBound: Offset,
    bottomBound: Offset
): PinRowSnapshot {
    return PinRowSnapshotImpl(
        pinRowState,
        pinState,
        topBound,
        bottomBound
    )
}

interface PinRowSnapshot {
    val pinRowState: PinRowState
    val pinState: PinState
    val topBound: Offset
    val bottomBound: Offset

    companion object {

        fun lazy(
            pinState: PinState,
            pinRowState: PinRowState,
            pinRowSnapshotProvider: PinRowSnapshotProvider
        ): PinRowSnapshot = LazyPinRowSnapshot(pinState, pinRowState, pinRowSnapshotProvider)

    }
}

private typealias PinRowSnapshotProvider = () -> PinRowSnapshot

private class LazyPinRowSnapshot(
    override val pinState: PinState,
    override val pinRowState: PinRowState,
    private val pinRowSnapshotProvider: PinRowSnapshotProvider
) : PinRowSnapshot {

    private var pinRowSnapshot: PinRowSnapshot? = null

    private fun initIfNull(): PinRowSnapshot {
        var snapshot = pinRowSnapshot

        if (snapshot != null) return snapshot

        snapshot = pinRowSnapshotProvider()
        pinRowSnapshot = snapshot

        return snapshot
    }

    override val topBound: Offset
        get() = initIfNull().topBound

    override val bottomBound: Offset
        get() = initIfNull().bottomBound

}

data class PinRowSnapshotImpl(
    override val pinRowState: PinRowState,
    override val pinState: PinState,
    override val topBound: Offset,
    override val bottomBound: Offset
) : PinRowSnapshot