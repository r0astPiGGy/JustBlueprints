package com.rodev.jbpkmp.di

import com.rodev.generator.action.entity.ActionType
import com.rodev.jbpkmp.data.*
import com.rodev.jbpkmp.domain.compiler.BlueprintCompiler
import com.rodev.jbpkmp.domain.compiler.Nodes
import com.rodev.jbpkmp.domain.remote.CodeUploadService
import com.rodev.jbpkmp.domain.repository.*
import com.rodev.jbpkmp.domain.source.*
import com.rodev.jbpkmp.domain.usecase.upload.BlueprintCompileUseCase
import com.rodev.jbpkmp.domain.usecase.upload.CodeUploadUseCase
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.*
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
    single<DynamicInvokableReferenceProvider> { DynamicInvokableReferenceProvider() }
    single<InvokableReferenceProvider> { get<DynamicInvokableReferenceProvider>() }
    single<NodeStateFactory>(createdAtStart = true) { NodeStateFactoryRegistry(get()).apply {
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

        // TODO Collapsible nodes
//        registerNodeStateFactoryByActionType(
//            actionType = ActionType.FACTORY, ArrayNodeStateFactory()
//        )

        registerNodeStateFactoryByActionType(
            actionType = ActionType.HIDDEN, InvokableDeclarationNodeStateFactory(
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

        // TODO Invokable references
        listOf(Nodes.Type.FUNCTION_REFERENCE, Nodes.Type.PROCESS_REFERENCE).forEach { nodeId ->
            registerNodeStateFactoryByNodeId(
                nodeId = nodeId, InvokableReferenceNodeStateFactory(
                    nodeDataSource = get(),
                    nodeTypeDataSource = get(),
                    actionDataSource = get(),
                    pinTypeDataSource = get(),
                    selectorDataSource = get(),
                    selectionHandler = get(),
                    actionDetailsDataSource = get(),
                    iconDataSource = get(),
                    invokableReferenceProvider = get()
                )
            )
        }

        registerNodeStateFactoryByNodeId(
            nodeId = Nodes.Factory.ARRAY.id, ArrayNodeStateFactory(
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
        registerNodeStateFactoryByNodeId(
            nodeId = VARIABLE_TYPE_TAG, VariableNodeStateFactory(
                selectionHandler = get(),
                variableStateProvider = get(),
                pinTypeDataSource = get()
            )
        )
    } }
    single<LocalProjectLoader> {
        LocalProjectLoaderImpl(get())
    }
    single<ViewPortViewModelFactory> {
        object : ViewPortViewModelFactory {

            override fun createEventGraphViewModel(): ViewPortViewModel {
                return ViewPortViewModel(
                    pinTypeComparator = get(),
                    nodeStateFactory = get(),
                    actionDataSource = get(),
                    nodeDataSource = get(),
                    iconDataSource = get(),
                    detailsDataSource = get()
                )
            }

            override fun createFunctionGraphViewModel(): ViewPortViewModel {
                return FunctionGraphViewModel(
                    pinTypeComparator = get(),
                    nodeStateFactory = get(),
                    actionDataSource = get(),
                    nodeDataSource = get(),
                    iconDataSource = get(),
                    detailsDataSource = get()
                )
            }

            override fun createProcessGraphViewModel(): ViewPortViewModel {
                return createFunctionGraphViewModel()
            }
        }
    }
    single<GraphStateFactory> { GraphStateFactoryImpl(get(), get(), get()) }

    factory<BlueprintCompiler> { BlueprintCompiler(get()) }
    factory<BlueprintCompileUseCase> { BlueprintCompileUseCase(get()) }
    factory<CodeUploadUseCase> { CodeUploadUseCase(get()) }
    factory<EditorScreenViewModel> { params ->
        val projectPath: String = params.get()
        val projectLoader: LocalProjectLoader = get()

        EditorScreenViewModel(
            projectReference = projectLoader.loadProjectFromFolder(projectPath),
            repository = get(),
            compile = get(),
            uploadCode = get(),
            selectionDispatcher = get(),
            graphStateFactory = get(),
            dynamicVariableStateProvider = get(),
            dynamicInvokableReferenceProvider = get()
        )
    }
    factory { SettingsScreenViewModel(get()) }
    factory {
        WelcomeScreenViewModel(
            projectLoader = get(),
            repository = get()
        )
    }
}