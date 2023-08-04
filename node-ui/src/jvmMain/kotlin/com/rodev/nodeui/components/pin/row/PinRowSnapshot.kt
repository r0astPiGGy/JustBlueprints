package com.rodev.nodeui.components.pin.row

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.rodev.nodeui.components.node.NodeState

interface SnapshotRequester {

    val snapshotRequested: Boolean

    fun addSnapshot(snapshot: PinRowSnapshot)

}

class DefaultSnapshotRequester(
    private val snapshotFilter: (PinRowSnapshot) -> Boolean
) : SnapshotRequester {

    private val mutableSnapshots = mutableListOf<PinRowSnapshot>()
    val snapshots: List<PinRowSnapshot>
        get() = mutableSnapshots

    override var snapshotRequested by mutableStateOf(false)

    override fun addSnapshot(snapshot: PinRowSnapshot) {
        if (snapshotFilter(snapshot)) {
            mutableSnapshots.add(snapshot)
        }
    }

    fun clearSnapshots() {
        mutableSnapshots.clear()
    }

}

fun PinRowSnapshot(
    nodeState: NodeState,
    pinRowState: PinRowState,
    topBound: Offset,
    bottomBound: Offset
): PinRowSnapshot {
    return PinRowSnapshotImpl(
        nodeState,
        pinRowState,
        topBound,
        bottomBound
    )
}

interface PinRowSnapshot {
    val nodeState: NodeState
    val pinRowState: PinRowState
    val topBound: Offset
    val bottomBound: Offset

    companion object {

        fun lazy(
            nodeState: NodeState,
            pinRowState: PinRowState,
            pinRowSnapshotProvider: PinRowSnapshotProvider
        ): PinRowSnapshot = LazyPinRowSnapshot(nodeState, pinRowState, pinRowSnapshotProvider)

    }
}

private typealias PinRowSnapshotProvider = () -> PinRowSnapshot

private class LazyPinRowSnapshot(
    override val nodeState: NodeState,
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
    override val nodeState: NodeState,
    override val pinRowState: PinRowState,
    override val topBound: Offset,
    override val bottomBound: Offset
) : PinRowSnapshot