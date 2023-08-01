package com.rodev.generator.action.interpreter.event

import com.rodev.generator.action.ActionLogger
import com.rodev.jmcc_extractor.entity.EventData
import com.rodev.jmcc_extractor.entity.GameValueData
import java.util.*
import java.util.function.Predicate

class EventDataMapper(
    private val eventData: List<EventData>,
    private val gameValues: List<GameValueData>
) {

    private val eventsById: MutableMap<String, EventData> = HashMap<String, EventData>()
    private val eventGroupsById: MutableMap<String, List<String>> = HashMap()
    private val mappedEventsByLocalizationId: MutableMap<String, String> = HashMap()
    private val gameValuesByEventId = hashMapOf<String, MutableList<GameValueData>>()

    init {
        mapEvents()
        mapEventGroups()
        mapEventsByLocalizationId()
        mapGameValuesByEventId()
    }

    private fun mapGameValuesByEventId() {
        for (gameValue in gameValues) {
            if (!gameValue.id.startsWith("event")) continue
            if (gameValue.worksWith == null) continue

            getApplicableEventsForValues(gameValue.worksWith!!)
                .stream()
                .distinct()
                .forEach { it.addGameValue(gameValue) }
        }
    }

    val EventData.gameValues: List<GameValueData>
        get() = gameValuesByEventId[id] ?: emptyList()

    private fun EventData.addGameValue(gameValueData: GameValueData) {
        gameValuesByEventId.computeIfAbsent(id) { mutableListOf() }.add(gameValueData)
    }

    private fun mapEvents() {
        eventData.forEach { eventsById[it.id] = it }
    }

    private fun mapEventGroups() {
        val map = eventGroupsById
        map["piston_events"] = filterEventsByPredicate { id: String ->
            id.contains(
                "piston"
            )
        }
        map["interact_events"] = object : LinkedList<String>() {
            init {
                add("player_interact")
                addAll(filterEventsByPredicate {
                    it.contains("click") && !it.contains(
                        "inventory"
                    )
                })
            }
        }
        map["place_break_events"] = listOf(
            "player_place_block",
            "player_break_block"
        )
        map["world_block_events"] = filterEventsByPredicate {
            it.contains(
                "block"
            )
        }
        map["explosion_events"] = listOf("block_explode")
        map["item_events"] = filterEventsByPredicate {
            it.contains(
                "item"
            )
        }
        map["death_events"] = filterEventsByPredicate {
            it.contains(
                "death"
            )
        }
    }

    private fun filterEventsByPredicate(predicate: Predicate<String>): List<String> {
        return eventsById.keys.stream().filter(predicate).toList()
    }

    private fun mapEventsByLocalizationId() {
        val map = mappedEventsByLocalizationId
        map["player_shoot_bow"] = "player_shot_bow"
        map["player_own_inventory_click"] = "player_click_own_inventory"
        map["player_other_inventory_click"] = "player_click_inventory"
    }

    private fun getApplicableEventsForValues(worksWith: List<String>): List<EventData> {
        val list = LinkedList<EventData>()
        for (eventId in worksWith) {
            list.addAll(getEventsById(eventId))
        }
        return list
    }

    private fun getEventDataGroupById(id: String): List<EventData> {
        val group = eventGroupsById[id]
        if (group == null) {
            ActionLogger.log("EventData group by outputPin $id not found.")
            return emptyList()
        }
        return group.mapNotNull { eventsById[it] }
    }

    private fun getEventsById(id: String): List<EventData> {
        if (id.endsWith("_events")) {
            return getEventDataGroupById(id)
        }

        val eventId = id.replace("_event", "")
        var ev = eventsById[eventId]

        if (ev != null) return listOf(ev)

        var tempId: String? = "player_$eventId"
        ev = eventsById[tempId]

        if (ev != null) return listOf(ev)

        tempId = mappedEventsByLocalizationId[eventId]
        ev = eventsById[tempId]

        if (ev != null) return listOf(ev)

        ActionLogger.log("EventData by outputPin $eventId not found. Is it group?")

        return listOf()
    }

}