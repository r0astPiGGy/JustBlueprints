package com.rodev.jbpkmp.di

import com.rodev.jbpkmp.data.*
import com.rodev.jbpkmp.domain.compiler.BlueprintCompiler
import com.rodev.jbpkmp.domain.remote.CodeUploadService
import com.rodev.jbpkmp.domain.repository.*
import com.rodev.jbpkmp.domain.usecase.upload.BlueprintCompileUseCase
import com.rodev.jbpkmp.domain.usecase.upload.CodeUploadUseCase
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.DefaultPinTypeComparator
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.ViewPortViewModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.ViewPortViewModelFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.ArrayNodeStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.DefaultNodeStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.NodeStateFactoryRegistry
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.VariableNodeStateFactory
import com.rodev.jbpkmp.presentation.screens.settings_screen.SettingsScreenViewModel
import com.rodev.jbpkmp.presentation.screens.welcome_screen.WelcomeScreenViewModel
import com.rodev.nodeui.components.graph.PinTypeComparator
import com.rodev.nodeui.components.node.NodeStateFactory
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val commonModule = module {
    single<Json> { Json { ignoreUnknownKeys = true } }
    single<CodeUploadService> { CodeUploadServiceImpl() }
    single<ProgramDataRepository> { ProgramDataRepositoryImpl() }
    single<NodeDataSource> { NodeDataSourceImpl(get()) }
    single<ActionDataSource> { ActionDataSourceImpl(get()) }
    single<IconDataSource> { IconDataSourceImpl(get()) }
    single<NodeTypeDataSource> { NodeTypeDataSourceImpl(get()) }
    single<PinTypeDataSource> { PinTypeDataSourceImpl(get()) }
    single<SelectorDataSource> { SelectorDataSourceImpl(get()) }
    single<ActionDetailsDataSource> { ActionDetailsDataSourceImpl(get()) }
    single<SelectionDispatcher> { SelectionDispatcher() }
    single<SelectionHandler> { get<SelectionDispatcher>() }
    single<PinTypeComparator> { DefaultPinTypeComparator }
    single<DynamicVariableStateProvider> { DynamicVariableStateProvider() }
    single<VariableStateProvider> { get<DynamicVariableStateProvider>() }
    single<NodeStateFactory>(createdAtStart = true) { NodeStateFactoryRegistry().apply {
        setDefaultNodeStateFactory(
            DefaultNodeStateFactory(
                nodeDataSource = get(),
                nodeTypeDataSource = get(),
                actionDataSource = get(),
                pinTypeDataSource = get(),
                selectorDataSource = get(),
                selectionHandler = get(),
                actionDetailsDataSource = get(),
                iconDataSource = get()
            )
        )
        registerNodeStateFactory(
            typeId = "native_array_factory", ArrayNodeStateFactory(
                nodeDataSource = get(),
                nodeTypeDataSource = get(),
                actionDataSource = get(),
                pinTypeDataSource = get(),
                selectorDataSource = get(),
                selectionHandler = get(),
                actionDetailsDataSource = get(),
                iconDataSource = get()
            )
        )
        registerNodeStateFactory(
            typeId = VARIABLE_TYPE_TAG, VariableNodeStateFactory(
                selectionHandler = get(),
                variableStateProvider = get(),
                pinTypeDataSource = get()
            )
        )
    } }
    single<ViewPortViewModelFactory> {
        ViewPortViewModelFactory {
            ViewPortViewModel(
                pinTypeComparator = get(),
                nodeStateFactory = get(),
                actionDataSource = get(),
                nodeDataSource = get(),
                iconDataSource = get()
            )
        }
    }

    factory<BlueprintCompiler> { BlueprintCompiler(get()) }
    factory<BlueprintCompileUseCase> { BlueprintCompileUseCase(get()) }
    factory<CodeUploadUseCase> { CodeUploadUseCase(get()) }
    factory { params -> EditorScreenViewModel(params.get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { SettingsScreenViewModel(get()) }
    factory { WelcomeScreenViewModel(get()) }
}