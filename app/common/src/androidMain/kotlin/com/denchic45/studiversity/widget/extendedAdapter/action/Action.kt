package com.denchic45.studiversity.widget.extendedAdapter.action

import androidx.recyclerview.widget.RecyclerView

interface Action {
    val adapter: RecyclerView.Adapter<*>
    val currentList: MutableList<Any>
    fun execute()
    fun undo()
}