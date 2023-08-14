package com.rodev.jbpkmp.data

import androidx.compose.ui.res.useResource
import com.rodev.generator.action.entity.Action
import com.rodev.generator.action.entity.Category
import com.rodev.jbpkmp.domain.repository.ActionDataSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
class ActionDataSourceImpl(json: Json) : ActionDataSource {

    private val categories = mutableListOf<Category>()
    private val actions = mutableListOf<Action>()

    private val categoriesByPath: MutableMap<String, Category> = hashMapOf()
    private val actionsByCategory: MutableMap<String, MutableList<Action>> = hashMapOf()
    private val actionsById: MutableMap<String, Action> = hashMapOf()

    init {
        useResource<List<Action>>(
            "data/actions.json",
            json::decodeFromStream
        ).let(actions::addAll)

        useResource<List<Category>>(
            "data/categories.json",
            json::decodeFromStream
        ).let(categories::addAll)

        categories.forEach {
            categoriesByPath[it.path] = it
        }

        actions.forEach {
            actionsByCategory
                .computeIfAbsent(it.category) { ArrayList() }
                .add(it)

            actionsById[it.id] = it
        }
    }

    override fun getAllActions(): List<Action> {
        return actions
    }

    override fun <T> transformActions(
        rootTransformFunction: (Category, List<T>) -> T,
        leafTransformFunction: (Action) -> T,
        filter: (Action) -> Boolean
    ) = ActionTransformHelper(
        actionsByCategory = actionsByCategory,
        categoriesByPath = categoriesByPath,
        rootTransformFunction,
        leafTransformFunction,
        filter
    ).transform()

    override fun getActionById(id: String): Action {
        return actionsById[id]!!
    }
}

private class ActionTransformHelper<T>(
    private val actionsByCategory: Map<String, List<Action>>,
    private val categoriesByPath: Map<String, Category>,
    private val rootTransformFunction: (Category, List<T>) -> T,
    private val leafTransformFunction: (Action) -> T,
    private val filter: (Action) -> Boolean
) {
    val rootComponent = CategoryWrapperRoot()

    fun transform(): List<T> {
        actionsByCategory.forEach(::addActionsToCategory)

        return rootComponent.transformChild()
    }

    fun addActionsToCategory(path: String, actions: List<Action>) {
        getCategoryWrapper(path)
            ?.actions
            ?.addAll(actions.filter(filter).map(leafTransformFunction))
    }

    fun getCategoryWrapper(path: String): CategoryWrapper? {
        val split = path.split(".")

        var categoryWrapper: CategoryWrapper = rootComponent
        for (s in split) {
            categoryWrapper = categoryWrapper.getChildCategory(s) {
                categoriesByPath[it]
            } ?: return null
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

        fun getChildCategory(
            path: String,
            categoryProvider: (String) -> Category?
        ): CategoryWrapper? {
            val resolvedPath = computePath(path)
            val category = categoryProvider(resolvedPath) ?: return null

            return childCategories.computeIfAbsent(path) {
                val child = CategoryWrapper(
                    path = resolvedPath,
                    category = category
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