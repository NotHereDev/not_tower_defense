@file:JvmName("NumberExtensions")
package fr.not_here.not_tower_defense.extensions

fun Number.isBetween(n1: Number, n2: Number): Boolean {
    val min = n1.toDouble().coerceAtMost(n2.toDouble())
    val max = n1.toDouble().coerceAtLeast(n2.toDouble())
    return this.toDouble() in min..max
}
