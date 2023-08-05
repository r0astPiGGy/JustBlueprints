package com.rodev.generator.action.interpreter.event

import com.rodev.generator.action.LocaleProvider
import com.rodev.generator.action.entity.*
import com.rodev.generator.action.entity.extra_data.EventExtraData
import com.rodev.generator.action.interpreter.ActionInterpreter
import com.rodev.jmcc_extractor.entity.EventData
import com.rodev.jmcc_extractor.entity.GameValueData

class EventInterpreter(
    private val localeProvider: LocaleProvider,
    private val eventDataMapper: EventDataMapper
) : ActionInterpreter<EventData> {
    override fun interpret(list: List<EventData>) = list.interpret(::interpretEvent)

    private fun interpretEvent(eventData: EventData): NodeCompound {
        val category = if (eventData.category == null) {
            "events"
        } else {
            "events.${eventData.category}"
        }

        return NodeCompound(
            id = "event_" + eventData.id,
            type = "event",
            name = localeProvider.translateEventName(eventData),
            category = category,
            input = emptyList(),
            output = eventData.output,
            iconPath = iconPathFrom("events", eventData.id),
            extra = eventData.extraData
        )
    }

    private val EventData.output: List<PinModel>
        get() {
            return mutableListOf(
                Pins.execPin("exec"),
            ).also {
                it.addAll(
                    with(eventDataMapper) {
                        this@output.gameValues.toPinModels()
                    }
                )
            }
        }

    private fun List<GameValueData>.toPinModels(): List<PinModel> {
        return map { it.toPinModel() }
    }

    private fun GameValueData.toPinModel(): PinModel {
        return PinModel(
            id = id,
            type = type,
            label = localeProvider.translateGameValue(this),
//            extra = outputPinExtraData
        )
    }

    private val EventData.extraData: EventExtraData
        get() = EventExtraData(cancellable)
}