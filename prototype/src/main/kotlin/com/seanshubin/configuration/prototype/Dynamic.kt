package com.seanshubin.configuration.prototype

object Dynamic {
    fun merge(left: Any?, right: Any?): Any? {
        if (left is Map<*, *> && right is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            left as Map<Any?, Any?>
            @Suppress("UNCHECKED_CAST")
            right as Map<Any?, Any?>
            return mergeMaps(left, right)
        } else {
            return right
        }
    }

    private fun mergeMaps(left: Map<Any?, Any?>, right: Map<Any?, Any?>): Map<Any?, Any?> {
        val keys = (left.keys + right.keys).distinct()
        val pairs: List<Pair<Any?, Any?>?> = keys.map {
            if (left.containsKey(it) && !right.containsKey(it)) {
                it to left[it]
            } else if (left.containsKey(it) && !right.containsKey(it)) {
                it to right[it]
            } else if (right.containsKey(it) && right[it] != null) {
                it to merge(left[it], right[it])
            } else {
                null
            }
        }
        return pairs.filterNotNull().toMap()
    }

    private fun Any?.toStringAndType(): String = "$this:${this?.javaClass?.simpleName ?: "null"}"

}
