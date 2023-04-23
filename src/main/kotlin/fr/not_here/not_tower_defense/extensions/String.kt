@file:JvmName("StringExtensions")
package fr.not_here.not_tower_defense.extensions

fun String.with(vararg values: Pair<String, Any>): String {
    var string = this
    for (value in values) {
        string = string.replace("{${value.first}}", value.second.toString())
    }
    return string
}