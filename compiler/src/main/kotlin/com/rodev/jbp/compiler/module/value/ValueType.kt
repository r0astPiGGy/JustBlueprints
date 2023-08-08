package com.rodev.jbp.compiler.module.value

enum class ValueType(val id: String) {
    Any("any"),
    Array("array"),
    GameValue("game_value"),
    Enum("enum"),
    Item("item"),
    Location("location"),
    Map("map"),
    Number("number"),
    Particle("particle"),
    Potion("potion"),
    Sound("sound"),
    Text("text"),
    Vector("vector"),
    Variable("variable"),
    Empty("empty")
}