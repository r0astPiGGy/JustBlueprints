package com.rodev.jbpkmp.data

import com.rodev.jbpkmp.domain.model.Action
import com.rodev.jbpkmp.domain.model.Category
import com.rodev.jbpkmp.domain.repository.ActionDataSource
import com.rodev.jbpkmp.util.randomNode
import com.rodev.nodeui.model.Node

class ActionDataSourceImpl : ActionDataSource {

    private val actions: List<Action> = listOf(
        Action("a", "A", "functions.example"),
        Action("b", "B", "functions.example"),
        Action("c", "C", "functions.example"),
        Action("e1", "Event", "events"),
        Action("e2", "Sample Event", "events.sample"),
        Action("e3", "Inner Event", "events.sample.inner"),
        Action("e4", "Inner Event2", "events.sample.inner"),
        Action("e5", "Inner Event3", "events.sample.inner"),
    )

    private val categories: List<Category> = listOf(
        Category("functions", "Functions"),
        Category("functions.example", "Examples"),
        Category("events", "Events"),
        Category("events.sample", "Sample"),
        Category("events.sample.inner", "Inner")
    )

    private val categoriesByPath: MutableMap<String, Category> = hashMapOf()
    private val actionsByCategory: MutableMap<String, MutableList<Action>> = hashMapOf()

    init {
        categories.forEach {
            categoriesByPath[it.path] = it
        }

        actions.forEach {
            actionsByCategory
                .computeIfAbsent(it.category) { ArrayList() }
                    .add(it)
        }
    }

    override fun getNodeById(id: String): Node {
        return randomNode()
    }

    override fun <T> getActions(
        rootTransformFunction: (Category, List<T>) -> T,
        leafTransformFunction: (Action) -> T
    ) = ActionTransformHelper(
        actionsByCategory = actionsByCategory,
        categoriesByPath = categoriesByPath,
        rootTransformFunction,
        leafTransformFunction
    ).transform()
}

private class ActionTransformHelper<T>(
    private val actionsByCategory: Map<String, List<Action>>,
    private val categoriesByPath: Map<String, Category>,
    private val rootTransformFunction: (Category, List<T>) -> T,
    private val leafTransformFunction: (Action) -> T
) {
    val rootComponent = CategoryWrapperRoot()

    fun transform(): List<T> {
        actionsByCategory.forEach(::addActionsToCategory)

        return rootComponent.transformChild()
    }

    fun addActionsToCategory(path: String, actions: List<Action>) {
        getCategoryWrapper(path)
            .actions
            .addAll(actions.map(leafTransformFunction))
    }

    fun getCategoryWrapper(path: String): CategoryWrapper {
        val split = path.split(".")

        var categoryWrapper: CategoryWrapper = rootComponent
        for (s in split) {
            categoryWrapper = categoryWrapper.getChildCategory(s) {
                categoriesByPath[it]!!
            }
        }

        return categoryWrapper
    }

    inner class CategoryWrapperRoot : CategoryWrapper(
        path = "",
        category = Category("", "")
    ) {
        override fun computePath(toResolve: String) = toResolve
    }

    open inner class CategoryWrapper(
        val path: String,
        val category: Category
    ) {
        val childCategories = mutableMapOf<String, CategoryWrapper>()
        val categories = mutableListOf<CategoryWrapper>()
        val actions = mutableListOf<T>()

        fun getChildCategory(path: String, categoryProvider: (String) -> Category): CategoryWrapper {
            return childCategories.computeIfAbsent(path) {
                val resolvedPath = computePath(path)

                val child = CategoryWrapper(
                    path = resolvedPath,
                    category = categoryProvider(resolvedPath)
                )

                categories += child

                child
            }
        }

        fun transform(): T {
            return rootTransformFunction(category, transformChild())
        }

        fun transformChild(): List<T> {
            val transformed = mutableListOf<T>()

            categories.map { it.transform() }.let(transformed::addAll)
            transformed.addAll(actions)

            return transformed
        }

        protected open fun computePath(toResolve: String): String {
            return "${this.path}.$toResolve"
        }
    }
}