package com.rodev.jmcc_extractor

import com.rodev.jmcc_extractor.entity.ActionData
import com.rodev.jmcc_extractor.entity.EventData
import com.rodev.jmcc_extractor.entity.GameValueData
import com.rodev.jmcc_extractor.entity.RawActionData
import com.rodev.jmcc_extractor.loader.RemoteDataLoader
import com.rodev.jmcc_extractor.loader.asJsonDataLoader
import com.rodev.jmcc_extractor.loader.asPropertyLoader

private const val actionDataUrl =
    "https://raw.githubusercontent.com/justmc-os/jmcc/main/data/actions.json"

private const val rawActionDataUrl =
    "https://raw.githubusercontent.com/justmc-os/justmc-assets/master/data/actions.json"

private const val localeDataUrl =
    "https://gitlab.com/justmc/justmc-localization/-/raw/master/creative_plus/ru_RU.properties"

private const val eventsDataUrl =
    "https://raw.githubusercontent.com/justmc-os/justmc-assets/master/data/events.json"

private const val gameValuesDataUrl =
    "https://raw.githubusercontent.com/justmc-os/justmc-assets/master/data/game_values.json"

private const val gameValuesMappingDataUrl =
    "https://raw.githubusercontent.com/justmc-os/jmcc/main/data/values.json"

val RawActionDataExtractor = RemoteDataLoader(rawActionDataUrl).asJsonDataLoader<List<RawActionData>>()
val ActionDataExtractor = RemoteDataLoader(actionDataUrl).asJsonDataLoader<List<ActionData>>()
val LocaleExtractor = RemoteDataLoader(localeDataUrl).asPropertyLoader()
val GameValueDataExtractor = RemoteDataLoader(gameValuesDataUrl).asJsonDataLoader<List<GameValueData>>()
val EventDataExtractor = RemoteDataLoader(eventsDataUrl).asJsonDataLoader<List<EventData>>()
