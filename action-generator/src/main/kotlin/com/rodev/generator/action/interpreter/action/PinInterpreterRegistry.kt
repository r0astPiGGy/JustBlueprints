package com.rodev.generator.action.interpreter.action

import com.rodev.generator.action.ActionLogger
import com.rodev.generator.action.LocaleProvider
import com.rodev.generator.action.entity.PinModel
import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.jmcc_extractor.entity.*

class PinInterpreterRegistry private constructor(
    private val localeProvider: LocaleProvider,
    private val alternateArgumentTypeProvider: AlternateArgumentTypeProvider = DefaultAlternateArgumentTypeProvider
): PinInterpreterRegistryBuilderScope {

    private val extraDataProviders = hashMapOf<String, PinExtraDataProvider>()
    private val defaultPinInterpreter = PinExtraDataProviderImpl { _, _, _ -> null }
    
    private fun findExtraDataProvider(type: String): PinExtraDataProvider {
        return extraDataProviders.getOrDefault(type, defaultPinInterpreter)
    }

    override fun registerPinExtraDataProvider(type: String, onProvide: OnProvideExtraData) {
        extraDataProviders[type] = PinExtraDataProviderImpl(onProvide)
    }
    
    fun interpretPin(actionData: ActionData, rawActionData: RawActionData, argument: Argument): PinModel {
        val name = localeProvider.translateArgName(actionData, argument)

        val id = argument.name.toString()
        val rawArgument = rawActionData.getArgumentById(id)
        var type = argument.type.toString()

        if (rawArgument != null) {
            val alternateType = alternateArgumentTypeProvider.getAlternateTypeFor(argument, rawArgument)
            if (alternateType != type) {
                ActionLogger.log("Found alternate type for argument $id in action ${actionData.id}: $type != $alternateType")
                type = alternateType
            }
        }

        val extraData = findExtraDataProvider(type).provideExtraData(actionData, argument, rawArgument)

        return PinModel(
            id = id,
            type = type,
            label = name,
            extra = extraData
        )
    }
    
    private class PinExtraDataProviderImpl(
        val onProvide: OnProvideExtraData
    ): PinExtraDataProvider {

        override fun provideExtraData(actionData: ActionData, argument: Argument, rawArgument: RawArgument?): ExtraData? {
            return onProvide.invoke(actionData, argument, rawArgument)
        }
    }
    
    companion object {
        
        fun build(localeProvider: LocaleProvider, scope: PinInterpreterRegistryBuilderScope.() -> Unit): PinInterpreterRegistry {
            return PinInterpreterRegistry(localeProvider).also(scope)
        }
        
    }
    
}

interface AlternateArgumentTypeProvider {

    fun getAlternateTypeFor(argument: Argument, rawArgument: RawArgument): String

}

object DefaultAlternateArgumentTypeProvider : AlternateArgumentTypeProvider {

    override fun getAlternateTypeFor(argument: Argument, rawArgument: RawArgument): String {
        val alternateType = rawArgument.type

        if (argument.type == "boolean" && alternateType == "enum") return "boolean"

        if (alternateType == "block") return "item"

        return alternateType
    }

}

interface PinInterpreterRegistryBuilderScope {

    fun registerPinExtraDataProvider(type: String, onProvide: OnProvideExtraData)

}

typealias OnProvideExtraData = (ActionData, Argument, RawArgument?) -> ExtraData?

interface PinExtraDataProvider {

    fun provideExtraData(actionData: ActionData, argument: Argument, rawArgument: RawArgument?): ExtraData?

}