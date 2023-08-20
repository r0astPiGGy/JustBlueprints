package com.rodev.jbpkmp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun <T> TabLayout(
    modifier: Modifier = Modifier,
    state: TabLayoutHostState<T> = remember { TabLayoutHostState() },
    tabs: @Composable (TabLayoutHostState<T>) -> Unit,
    content: @Composable (Tab<T>?) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        tabs(state)
        content(state.currentTab)
    }
}

class TabLayoutHostState<T>(
    initialTabs: List<Tab<T>> = emptyList()
) {

    private val mutableTabs = mutableStateListOf<Tab<T>>().apply { addAll(initialTabs) }
    val tabs: List<Tab<T>>
        get() = mutableTabs

    var currentTab: Tab<T>? by mutableStateOf(null)

    init {
        selectFirstTab()
    }

    private fun disableCurrentTab() {
        currentTab?.let {
            it.selected = false
        }
        currentTab = null
    }

    private fun selectFirstTab() {
        if (mutableTabs.isNotEmpty()) {
            disableCurrentTab()
            selectTab(mutableTabs.first())
        }
    }

    fun addTab(tabState: Tab<T>) {
        mutableTabs.add(tabState)

        if (mutableTabs.size == 1) {
            selectFirstTab()
        }
    }

    fun openTab(index: Int) {
        val tab = tabs.getOrNull(index) ?: return

        selectTab(tab)
    }

    val currentTabIndex: Int
        get() = tabs.indexOf(currentTab)

    fun openTab(tabState: Tab<T>) {
        val targetTab = mutableTabs.firstOrNull { it.data == tabState.data }

        if (targetTab != null) {
            selectTab(targetTab)
            return
        }

        if (!mutableTabs.contains(tabState)) {
            mutableTabs.add(tabState)
        }

        selectTab(tabState)
    }

    fun openTabIf(predicate: (Tab<T>) -> Boolean): Boolean {
        val targetTab = mutableTabs.firstOrNull(predicate)

        if (targetTab != null) {
            selectTab(targetTab)
            return true
        }

        return false
    }

    private fun selectTab(tabState: Tab<T>) {
        disableCurrentTab()
        tabState.selected = true
        currentTab = tabState
    }

    fun addTabs(tabsToAdd: List<Tab<T>>) {
        val wasEmpty = mutableTabs.isEmpty()

        mutableTabs.addAll(tabsToAdd)

        if (wasEmpty) {
            selectFirstTab()
        }
    }

    fun removeIf(predicate: (Tab<T>) -> Boolean) {
        mutableTabs.firstOrNull(predicate)?.let(::onTabClosed)
    }

    fun onTabClosed(tabState: Tab<T>) {
        if (tabState != currentTab) {
            mutableTabs.remove(tabState)
            return
        }

        tabState.selected = false

        val index = mutableTabs.indexOf(tabState)

        mutableTabs.removeAt(index)

        val tab = mutableTabs.getOrNull(index - 1) ?: mutableTabs.getOrNull(index + 1)

        tab?.let {
            it.selected = true
            currentTab = it
        }
    }

    fun onTabSelected(tabState: Tab<T>) {
        if (currentTab == tabState) return

        disableCurrentTab()

        tabState.selected = true
        currentTab = tabState
    }

}

interface Tab<T> {

    val name: String
    val closeable: Boolean
    val data: T
    var selected: Boolean

}

class TabState<T>(
    override val name: String,
    override val closeable: Boolean = true,
    override val data: T
) : Tab<T> {

    override var selected: Boolean by mutableStateOf(false)

}