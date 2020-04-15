package com.droid47.petfriend.base.extensions

infix fun <T> Collection<T>.sameContentWith(collection: Collection<T>?) =
    collection?.let { this.size == it.size && this.containsAll(it) } ?: false

fun <T> Collection<T>.toStringWithoutBrackates() =
    this.toString().replace("[", "").replace("]", "")

fun <E> List<E>.random(random: java.util.Random): E? =
    if (size > 0) get(random.nextInt(size)) else null
