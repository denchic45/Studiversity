package com.denchic45.kts.data.mapper

object ListMapper {
    fun fromList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    fun fromList2(list: List<String>?): String {
        return list?.joinToString(separator = "','", prefix = "'", postfix = "'") ?: ""
    }

    fun tolList(s: String): List<String> {
        return if (s.isEmpty())
            emptyList()
        else
            listOf(*s.split(",".toRegex()).toTypedArray())
    }

}