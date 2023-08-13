package com.rodev.nodeui.components.graph

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.chihsuanwu.freescroll.FreeScrollState
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.row.DefaultSnapshotRequester
import com.rodev.nodeui.components.pin.row.PinRowSnapshot
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.components.wire.Wire
import com.rodev.nodeui.components.wire.WireFactory
import com.rodev.nodeui.model.Graph

open class GraphViewModel(
    initialScrollX: Int = 0,
    initialScrollY: Int = 0,
    pinTypeComparator: PinTypeComparator = PinTypeComparator.Default,
    private val nodeStateFactory: NodeStateFactory,
    private val wireFactory: WireFactory = WireFactory()
) : PinDragListener {

    private val pinConnectionHandler = PinConnectionHandler(
        wireFactory = wireFactory,
        pinTypeComparator = pinTypeComparator
    )

    val scrollState = FreeScrollState(
        ScrollState(initialScrollX),
        ScrollState(initialScrollY)
    )

    val scrollOffset: Offset
        get() = Offset(scrollState.xValue.toFloat(), scrollState.yValue.toFloat())

    private val snapshotRequester = DefaultSnapshotRequester(::shouldAddPinRowSnapshot)
    private val graphFactory = GraphFactory(nodeStateFactory)
    private val _nodeStates = mutableStateListOf<NodeState>()
    val nodeStates: List<NodeState>
        get() = _nodeStates

    var temporaryWire by mutableStateOf<Wire?>(null)
        private set

    val wires: List<Wire>
        get() = pinConnectionHandler.wires

    private var cachedHitTest: PinRowSnapshot? = null

    private var currentDraggingPinOwner: NodeState? = null
    private var currentDraggingPin: PinState? = null

    private var currentHoveringPin: PinState? = null
    private var currentHoveringRow: PinRowState? = null

    open fun onEvent(event: GraphEvent) {
        when (event) {
            is NodeAddEvent -> {
                val node = nodeStateFactory.createNodeState(event.node)
                onNodeAdd(node)
            }

            NodeClearEvent -> {
                clearNodes()
            }

            else -> {}
        }
    }

    protected open fun onNodeAdd(node: NodeState) {
        node.initListeners()
        _nodeStates.add(node)
    }

    fun save(): Graph {
        return graphFactory.save(_nodeStates, pinConnectionHandler.mutableWires)
    }

    fun load(graph: Graph) {
        clearNodes()

        val nodes = graphFactory.load(graph, pinConnectionHandler::connect)
        nodes.forEach { it.initListeners() }
        _nodeStates.addAll(nodes)
    }

    private fun NodeState.initListeners() {
        snapshotRequester = this@GraphViewModel.snapshotRequester
        setPinDragListener(this@GraphViewModel)
    }

    private fun shouldAddPinRowSnapshot(snapshot: PinRowSnapshot): Boolean {
        return pinConnectionHandler.shouldAddSnapshot(snapshot, currentDraggingPin, currentDraggingPinOwner)
    }

    fun deleteNode(nodeState: NodeState) {
        pinConnectionHandler.disconnectAll(nodeState)
        _nodeStates.remove(nodeState)
    }

    fun deleteNodes(nodes: List<NodeState>) {
        nodes.forEach { pinConnectionHandler.disconnectAll(it) }
        _nodeStates.removeAll(nodes)
    }

    private fun clearNodes() {
        _nodeStates.forEach(pinConnectionHandler::disconnectAll)
        _nodeStates.clear()
    }

    override fun onPinDragStart(pinOwner: NodeState, pinState: PinState) {
        pinConnectionHandler.onDragStart(pinState)

        currentDraggingPinOwner = pinOwner
        currentDraggingPin = pinState
        snapshotRequester.snapshotRequested = true
    }

    override fun onPinDrag(
        sourceAbsolutePosition: Offset,
        pinState: PinState,
        offset: Offset,
        change: PointerInputChange
    ) {
        require(pinState == currentDraggingPin)

        val start = pinState.center

        val endX = sourceAbsolutePosition.x + change.position.x
        val endY = sourceAbsolutePosition.y + change.position.y

        val pinRowSnapshot = hitTest(endX, endY)
        val hoveredRow = pinRowSnapshot?.pinRowState
        val hoveredPin = hoveredRow?.pinState

        if (hoveredRow != null && hoveredPin != null) {
            if (hoveredPin == currentDraggingPin) return

            clearCurrentHoveringRow()

            temporaryWire = wireFactory.createWirePreview(
                pinState.pinDisplay.color,
                hoveredPin.pinDisplay.color,
                startPos = Offset(
                    x = start.x,
                    y = start.y
                ),
                endPos = Offset(
                    x = hoveredPin.center.x,
                    y = hoveredPin.center.y
                )
            )

            currentHoveringRow = hoveredRow
            currentHoveringPin = hoveredPin
            hoveredRow.hovered = true
        } else {
            clearCurrentHoveringRow()
            currentHoveringPin = null
            temporaryWire = wireFactory.createTemporaryWire(
                pinState.pinDisplay.color,
                start = Offset(
                    x = start.x,
                    y = start.y
                ),
                end = Offset(
                    x = endX,
                    y = endY
                )
            )
        }
    }

    private fun clearCurrentHoveringRow() {
        val lastHoveredRow = currentHoveringRow
        if (lastHoveredRow != null) {
            lastHoveredRow.hovered = false
            currentHoveringRow = null
        }
    }

    private fun hitTest(x: Float, y: Float): PinRowSnapshot? {
        val lastHitTest = cachedHitTest
        if (lastHitTest != null && lastHitTest.isInBounds(x, y)) {
            return lastHitTest
        } else {
            cachedHitTest = null
        }

        for (pinSnapshot in snapshotRequester.snapshots) {
            if (pinSnapshot.isInBounds(x, y)) {
                cachedHitTest = pinSnapshot
                return pinSnapshot
            }
        }
        return null
    }

    private fun PinRowSnapshot.isInBounds(x: Float, y: Float): Boolean {
        return x in topBound.x..bottomBound.x && y in topBound.y..bottomBound.y
    }

    open fun onPinDragEndWithoutConnection(pinState: PinState) {}

    override fun onPinDragEnd() {
        val currentDraggingPin = currentDraggingPin!!

        val result = pinConnectionHandler.onConnection(currentDraggingPin, currentHoveringPin)

        if (!result) {
            onPinDragEndWithoutConnection(currentDraggingPin)
        }

        this.currentDraggingPin = null
        currentHoveringPin = null
        currentDraggingPinOwner = null
        cachedHitTest = null
        snapshotRequester.snapshotRequested = false
        clearCurrentHoveringRow()
        temporaryWire = null

        snapshotRequester.clearSnapshots()
    }


}